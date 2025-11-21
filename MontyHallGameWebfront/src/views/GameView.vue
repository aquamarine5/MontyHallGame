<!--
 * @Author: aquamarine5 && aquamarine5_@outlook.com
 * Copyright (c) 2025 by @aquamarine5, RC. All Rights Reversed.
 * lovely lonely, but be a quokka.
-->
<script setup>
import { ref, computed } from 'vue';

const classicDoors = ref([]);
const classicGameState = ref('initial'); // initial, selected, revealed, finished
const classicUserChoice = ref(null);
const classicHostOpened = ref(null);
const classicFinalResult = ref(''); // 'win' or 'lose'

function setupDoors() {
  const prizeDoor = Math.floor(Math.random() * 3) + 1;
  classicDoors.value = [
    { id: 1, hasPrize: 1 === prizeDoor, isOpen: false, isSelected: false },
    { id: 2, hasPrize: 2 === prizeDoor, isOpen: false, isSelected: false },
    { id: 3, hasPrize: 3 === prizeDoor, isOpen: false, isSelected: false },
  ];
}

function resetClassicGame() {
  classicGameState.value = 'initial';
  classicUserChoice.value = null;
  classicHostOpened.value = null;
  classicFinalResult.value = '';
  setupDoors();
}

function classicSelectDoor(doorId) {
  if (classicGameState.value !== 'initial') return;

  classicUserChoice.value = doorId;
  classicDoors.value.find(d => d.id === doorId).isSelected = true;
  classicGameState.value = 'selected';

  setTimeout(hostOpenDoor, 500);
}

function hostOpenDoor() {
  const options = classicDoors.value.filter(d => d.id !== classicUserChoice.value && !d.hasPrize);

  let doorToOpen;
  if (options.length > 0) {
    doorToOpen = options[0].id;
  } else {
    const otherDoors = classicDoors.value.filter(d => d.id !== classicUserChoice.value);
    doorToOpen = otherDoors[Math.floor(Math.random() * otherDoors.length)].id;
  }

  classicHostOpened.value = doorToOpen;
  classicDoors.value.find(d => d.id === doorToOpen).isOpen = true;
  classicGameState.value = 'revealed';
}

async function sendResult(params) {
  const url = new URL(`${window.location.protocol}//${window.location.hostname}:1214/result`);
  url.search = new URLSearchParams(params).toString();
  try {
    await fetch(url);
  } catch (error) {
    console.error('Failed to send result:', error);
    alert("无法发送结果，请检查网络或目标服务是否运行。");
  }
}

function classicFinalChoice(strategy) {
  if (classicGameState.value !== 'revealed') return;

  let finalDoorId;
  if (strategy === 'stick') {
    finalDoorId = classicUserChoice.value;
  } else {
    finalDoorId = classicDoors.value.find(d =>
      d.id !== classicUserChoice.value && d.id !== classicHostOpened.value
    ).id;
    classicDoors.value.find(d => d.id === classicUserChoice.value).isSelected = false;
    classicDoors.value.find(d => d.id === finalDoorId).isSelected = true;
  }

  const finalDoor = classicDoors.value.find(d => d.id === finalDoorId);
  const hasWon = finalDoor.hasPrize;
  classicFinalResult.value = hasWon ? 'win' : 'lose';

  const params = {};
  if (strategy === 'stick') {
    params.first = hasWon;
  } else {
    params.second = hasWon;
  }
  sendResult(params);

  classicDoors.value.forEach(d => d.isOpen = true);
  classicGameState.value = 'finished';
}


// --- 朋友开门场景状态 ---
const friendDoors = ref([]);
const friendGameState = ref('initial'); // initial, selected, revealed, finished
const friendUserChoice = ref(null);
const friendOpened = ref(null);
const friendFinalResult = ref(''); // 'win' or 'lose'
const friendDecision = ref(''); // 'stick' or 'switch'
const friendFinalDoorId = ref(null);

function setupFriendDoors() {
  const prizeDoor = Math.floor(Math.random() * 3) + 1;
  friendDoors.value = [
    { id: 1, hasPrize: 1 === prizeDoor, isOpen: false, isSelected: false },
    { id: 2, hasPrize: 2 === prizeDoor, isOpen: false, isSelected: false },
    { id: 3, hasPrize: 3 === prizeDoor, isOpen: false, isSelected: false },
  ];
}

function resetFriendGame() {
  friendGameState.value = 'initial';
  friendUserChoice.value = null;
  friendOpened.value = null;
  friendFinalResult.value = '';
  friendDecision.value = '';
  friendFinalDoorId.value = null;
  setupFriendDoors();
}

function friendSelectDoor(doorId) {
  if (friendGameState.value !== 'initial') return;

  friendUserChoice.value = doorId;
  friendDoors.value.find(d => d.id === doorId).isSelected = true;
  friendGameState.value = 'selected';

  setTimeout(friendOpenDoor, 500);
}

function friendOpenDoor() {
  const options = friendDoors.value.filter(d => d.id !== friendUserChoice.value && !d.hasPrize);

  let doorToOpenId;
  if (options.length > 0) {
    doorToOpenId = options[0].id;
  } else {
    const otherDoors = friendDoors.value.filter(d => d.id !== friendUserChoice.value);
    doorToOpenId = otherDoors[Math.floor(Math.random() * otherDoors.length)].id;
  }

  friendOpened.value = doorToOpenId;
  friendDoors.value.find(d => d.id === doorToOpenId).isOpen = true;

  setTimeout(() => applyFriendStrategyAndFinish(doorToOpenId), 500);
}

function applyFriendStrategyAndFinish(openedDoorId) {
  const otherUnopenedDoor = friendDoors.value.find(d =>
    d.id !== friendUserChoice.value && d.id !== openedDoorId
  );

  if (openedDoorId < otherUnopenedDoor.id) {
    friendDecision.value = 'stick';
    friendFinalDoorId.value = friendUserChoice.value;
  } else {
    friendDecision.value = 'switch';
    friendFinalDoorId.value = otherUnopenedDoor.id;

    friendDoors.value.find(d => d.id === friendUserChoice.value).isSelected = false;
    friendDoors.value.find(d => d.id === friendFinalDoorId.value).isSelected = true;
  }

  friendGameState.value = 'revealed';

  setTimeout(() => {
    const finalDoor = friendDoors.value.find(d => d.id === friendFinalDoorId.value);
    const hasWon = finalDoor.hasPrize;
    friendFinalResult.value = hasWon ? 'win' : 'lose';

    sendResult({ third: hasWon });

    friendDoors.value.forEach(d => d.isOpen = true);
    friendGameState.value = 'finished';
  }, 800);
}


setupDoors();
setupFriendDoors();

</script>

<template>
  <main class="game-container">
    <h1>三门游戏</h1>

    <div class="game-section">
      <h2>场景一：经典三门问题</h2>
      <p v-if="classicGameState === 'initial'">请选择一扇门：</p>
      <p v-if="classicGameState === 'selected'">您选择了 {{ classicUserChoice }} 号门。主持人正在打开一扇空门...</p>
      <p v-if="classicGameState === 'revealed'">主持人打开了 {{ classicHostOpened }} 号门，里面是空的。您要坚持还是更换选择？</p>

      <div class="doors">
        <div v-for="door in classicDoors" :key="door.id" class="door" :class="{
          selected: door.isSelected,
          opened: door.isOpen
        }" @click="classicSelectDoor(door.id)">
          <div class="door-front">
            <span class="door-number">{{ door.id }}</span>
          </div>
          <div class="door-back">
            <span v-if="door.isOpen || classicGameState === 'finished'">{{ door.hasPrize ? '🔑' : '📦' }}</span>
          </div>
        </div>
      </div>

      <div class="controls" v-if="classicGameState === 'revealed'">
        <button @click="classicFinalChoice('stick')">坚持选择 {{ classicUserChoice }} 号门</button>
        <button @click="classicFinalChoice('switch')">
          改选另一扇门
        </button>
      </div>

      <div v-if="classicGameState === 'finished'" class="result">
        <h3 v-if="classicFinalResult === 'win'">恭喜你！你赢得了钥匙！🎉</h3>
        <h3 v-else>很遗憾，你选到了一个空箱子。😕</h3>
        <button @click="resetClassicGame">再玩一次</button>
      </div>
    </div>

    <div class="game-section">
      <h2>场景二：朋友开门策略</h2>
      <p v-if="friendGameState === 'initial'">请选择一扇门：</p>
      <p v-if="friendGameState === 'selected'">您选择了 {{ friendUserChoice }} 号门。您的朋友将随机打开一扇门...</p>
      <p v-if="friendGameState === 'revealed'">朋友打开了 {{ friendOpened }} 号门。根据策略，决定为“{{ friendDecision === 'stick' ?
        '坚持选择' : '改选' }}”。</p>

      <div class="doors">
        <div v-for="door in friendDoors" :key="door.id" class="door" :class="{
          selected: door.isSelected,
          opened: door.isOpen,
          'final-choice': friendGameState === 'finished' && door.id === friendFinalDoorId
        }" @click="friendSelectDoor(door.id)">
          <div class="door-front">
            <span class="door-number">{{ door.id }}</span>
          </div>
          <div class="door-back">
            <span>{{ door.hasPrize ? '🔑' : '📦' }}</span>
          </div>
        </div>
      </div>

      <div v-if="friendGameState === 'finished'" class="result">
        <h3 v-if="friendFinalResult === 'win'">恭喜你！你赢得了钥匙！🎉</h3>
        <h3 v-else>很遗憾，你选到了一个空箱子。😕</h3>
        <button @click="resetFriendGame">再玩一次</button>
      </div>
    </div>

    <div class="stats-link">
      <router-link to="/result">
        <button>查看统计</button>
      </router-link>
    </div>
  </main>
</template>

<style>
.game-container {
  max-width: 800px;
  margin: 0rem auto;
  padding: 1rem;
  text-align: center;
  font-family: sans-serif;
}

.stats-link {
  margin-top: 1rem;
}

.game-section {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 2rem;
}

.doors {
  display: flex;
  justify-content: space-around;
  margin: 2rem 0;
}

.door {
  margin: 0px 5px;
  width: 100px;
  height: 150px;
  border: 2px solid #555;
  border-radius: 5px;
  cursor: pointer;
  position: relative;
  perspective: 1000px;
  transition: transform 0.2s;
}

.door:not(.opened):hover {
  transform: scale(1.05);
}

.door.selected {
  border-color: #42b983;
  box-shadow: 0 0 15px #42b983;
}

.door.final-choice {
  border-color: #42b983;
  box-shadow: 0 0 20px #42b983;
}

.door-front,
.door-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 2rem;
  transition: transform 0.8s;
  transform-style: preserve-3d;
}

.door-front {
  background-color: #a0522d;
  color: white;
}

.door-back {
  background-color: #f0e68c;
  transform: rotateY(180deg);
}

.door.opened .door-front {
  transform: rotateY(-180deg);
}

.door.opened .door-back {
  transform: rotateY(0deg);
}


.door-number {
  font-size: 3rem;
  font-weight: bold;
}

.controls button,
.result button,
.stats-link button {
  padding: 10px 20px;
  font-size: 1rem;
  margin: 10px 10px;
  cursor: pointer;
  border: 1px solid #42b983;
  background-color: #42b983;
  color: white;
  border-radius: 5px;
  transition: background-color 0.3s;
}

.controls button:hover,
.result button:hover,
.stats-link button:hover {
  background-color: #36a476;
}

.result {
  margin-top: 1.5rem;
}

.result h3 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
}
</style>
