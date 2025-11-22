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

use([
    CanvasRenderer,
    BarChart,
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    GridComponent,
]);

const chartOption = ref({});

async function fetchDataAndDrawChart() {
    try {
        const response = await fetch(`${window.location.protocol}//${window.location.hostname}:1214/records`);
        if (!response.ok) {
            alert("无法获取统计数据，请检查网络或目标服务是否运行。");
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();

        const categories = ['策略1\n(坚持原门)', '策略2\n(始终改选)', '策略3\n(条件改选)'];
        const winsData = [data.firstGameWins, data.secondGameWins, data.thirdGameWins];
        const countsData = [data.firstGameCount, data.secondGameCount, data.thirdGameCount];

        const lossesData = countsData.map((count, i) => count - winsData[i]);
        const percentages = countsData.map((count, i) => {
            if (count === 0) return '0%';
            return `${((winsData[i] / count) * 100).toFixed(1)}%`;
        });

        const seriesData = [
            { name: '策略1-成功', color: '#3366CC', data: [winsData[0], 0, 0] },
            { name: '策略1-失败', color: '#87CEFA', data: [lossesData[0], 0, 0] },
            { name: '策略2-成功', color: '#228B22', data: [0, winsData[1], 0] },
            { name: '策略2-失败', color: '#98FB98', data: [0, lossesData[1], 0] },
            { name: '策略3-成功', color: '#FF8C00', data: [0, 0, winsData[2]] },
            { name: '策略3-失败', color: '#FFDAB9', data: [0, 0, lossesData[2]] },
        ];

        chartOption.value = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                },
                formatter: function (params) {
                    const categoryName = params[0].axisValueLabel;
                    let tooltipHtml = `${categoryName}<br/>`;

                    params.forEach(param => {
                        if (param.seriesName !== '胜率' && param.value > 0) {
                            tooltipHtml += `${param.marker} ${param.seriesName}: ${param.value}<br/>`;
                        }
                    });

                    return tooltipHtml;
                }
            },
            legend: {
                data: ['策略1-成功', '策略2-成功', '策略3-成功', '策略1-失败', '策略2-失败', '策略3-失败'],
                orient: 'horizontal',
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
                ...seriesData.map(s => ({
                    name: s.name,
                    type: 'bar',
                    stack: 'total',
                    label: {
                        show: true,
                        position: 'inside',
                        formatter: (params) => (params.value > 0 ? params.value : '')
                    },
                    emphasis: {
                        focus: 'series'
                    },
                    data: s.data,
                    itemStyle: {
                        color: s.color
                    }
                })),
                {
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
                    data: [0, 0, 0]
                }
            ]
        };

    } catch (error) {
        console.error("无法获取统计数据:", error);
        alert("无法获取统计数据，请检查网络或目标服务是否运行。");
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
    margin-top: 1rem;
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
