<!--
 * @Author: aquamarine5 && aquamarine5_@outlook.com
 * Copyright (c) 2025 by @aquamarine5, RC. All Rights Reversed.
 * lovely lonely, but be a quokka.
-->
<script setup>
import { RouterLink, RouterView } from 'vue-router'
import LineMdGithubTwotone from '~icons/line-md/github-twotone?width=32px&height=32px';

function jumpToGithub() {
  window.open("https://github.com/aquamarine5/MontyHallGame", "_blanks")
}
</script>

<template>
  <header>
    <div class="topbar_bg">
      <div class="topbar_container">
        <div class="topbar_title">
          三门问题模拟器
          <div class="topbar_version">
            v1.0 (251121)
          </div>
        </div>
        <LineMdGithubTwotone class="topbar_github" @click="jumpToGithub" />
      </div>
    </div>
  </header>

  <RouterView />
</template>

<style scoped>
.topbar_bg {
  background-color: #03a9f4;
  border-radius: 0px 0px 10px 10px;
}

.topbar_version {
  font-size: small;
  padding-left: 2px;
}

.topbar_container {
  padding-block: 8px;
  align-items: center;
  padding-inline: 16px;
  display: flex;
  height: 40px;
}

.topbar_title {
  display: flex;
  font-family: 'Gilroy', sans-serif;
  font-weight: 800;
  font-size: 20px;
  color: whitesmoke;
}

.topbar_github {
  cursor: pointer;
  justify-self: end;
  margin-left: auto;
}
</style>
