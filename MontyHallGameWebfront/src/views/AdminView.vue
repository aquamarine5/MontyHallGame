<template>
    <main class="admin-container">
        <h1>管理面板</h1>
        <p class="warn">如果你不知道这个按钮意味着什么，请不要点击。</p>

        <div class="actions">
            <button @click="clearRecords" class="danger">清除记录</button>
        </div>
    </main>
</template>

<script setup>
// 发送清除请求到当前域名的1214端口
async function clearRecords() {
    const ok = window.confirm('确认要清除所有记录？此操作不可撤销。');
    if (!ok) return;

    const url = `${window.location.protocol}//${window.location.hostname}:1214/clear`;
    try {
        const res = await fetch(url, { method: 'GET' });
        if (!res.ok) {
            alert('清除请求失败：' + res.status);
            return;
        }
        alert('清除请求已发送。');
    } catch (e) {
        console.error('Clear request failed', e);
        alert('无法发送清除请求，请检查网络或目标服务是否运行。');
    }
}
</script>

<style scoped>
.admin-container {
    max-width: 720px;
    margin: 2rem auto;
    padding: 1.5rem;
    text-align: center;
    font-family: sans-serif;
}

.warn {
    color: #b03030;
    font-weight: bold;
    margin-bottom: 1rem;
}

.actions {
    margin-top: 1.5rem;
}

.danger {
    padding: 10px 20px;
    background: #d9534f;
    border: 1px solid #b52b2b;
    color: white;
    border-radius: 6px;
    cursor: pointer;
}

.danger:hover {
    background: #c9302c
}
</style>
