<!--
 * @Author: aquamarine5 && aquamarine5_@outlook.com
 * Copyright (c) 2025 by @aquamarine5, RC. All Rights Reversed.
 * lovely lonely, but be a quokka.
-->
<template>
    <div class="result-container">
        <h1>游戏结果统计</h1>
        <v-chart class="chart" :option="chartOption" autoresize />
        <div class="game-link">
            <router-link to="/">
                <button>返回游戏</button>
            </router-link>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { BarChart } from 'echarts/charts';
import {
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    GridComponent,
} from 'echarts/components';
import VChart, { THEME_KEY } from 'vue-echarts';

// 注册 ECharts 组件
use([
    CanvasRenderer,
    BarChart,
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    GridComponent,
]);

const chartOption = ref({});

// 获取并处理数据
async function fetchDataAndDrawChart() {
    try {
        const response = await fetch(`${window.location.protocol}//${window.location.hostname}:1214/records`);
        if (!response.ok) {
            alert("无法获取统计数据，请检查网络或目标服务是否运行。");
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();

        const categories = ['策略1 (坚持原门)', '策略2 (始终改选)', '策略3 (条件改选)'];
        const winsData = [data.firstGameWins, data.secondGameWins, data.thirdGameWins];
        const countsData = [data.firstGameCount, data.secondGameCount, data.thirdGameCount];

        const lossesData = countsData.map((count, i) => count - winsData[i]);
        const percentages = countsData.map((count, i) => {
            if (count === 0) return '0%';
            return `${((winsData[i] / count) * 100).toFixed(1)}%`;
        });

        // 设置 ECharts 配置
        chartOption.value = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            legend: {
                data: ['成功', '失败'],
                top: 'top'
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: {
                type: 'category',
                data: categories
            },
            yAxis: {
                type: 'value'
            },
            series: [
                {
                    name: '成功',
                    type: 'bar',
                    stack: 'total',
                    label: {
                        show: true,
                        position: 'inside'
                    },
                    emphasis: {
                        focus: 'series'
                    },
                    data: winsData,
                    itemStyle: {
                        color: function (params) {
                            const colorList = ['#3366CC', '#228B22', '#FF8C00']; // 深蓝, 深绿, 深橙
                            return colorList[params.dataIndex];
                        }
                    }
                },
                {
                    name: '失败',
                    type: 'bar',
                    stack: 'total',
                    label: {
                        show: true,
                        position: 'inside'
                    },
                    emphasis: {
                        focus: 'series'
                    },
                    data: lossesData,
                    itemStyle: {
                        color: function (params) {
                            const colorList = ['#87CEFA', '#98FB98', '#FFDAB9']; // 浅蓝, 浅绿, 浅橙
                            return colorList[params.dataIndex];
                        }
                    }
                },
                {
                    // 这个系列是“幽灵”系列，专门用来在顶部显示百分比
                    name: '胜率',
                    type: 'bar',
                    stack: 'total',
                    silent: true,
                    tooltip: {
                        show: false
                    },
                    label: {
                        show: true,
                        position: 'top',
                        formatter: (params) => percentages[params.dataIndex],
                        color: '#333',
                        fontSize: 14,
                        fontWeight: 'bold'
                    },
                    data: [0, 0, 0] // 数据为0，不显示柱子
                }
            ]
        };

    } catch (error) {
        console.error("无法获取统计数据:", error);
        // 可以在这里设置一个错误状态，并在UI上显示
    }
}

onMounted(() => {
    fetchDataAndDrawChart();
});
</script>

<style scoped>
.result-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 0rem 2rem;
    font-family: sans-serif;
}

.chart {
    height: 500px;
    width: 100%;
    max-width: 800px;
}

.game-link {
    margin-top: 2rem;
}

.game-link button {
    padding: 10px 20px;
    font-size: 1rem;
    cursor: pointer;
    border: 1px solid #42b983;
    background-color: #42b983;
    color: white;
    border-radius: 5px;
    transition: background-color 0.3s;
}

.game-link button:hover {
    background-color: #36a476;
}
</style>
