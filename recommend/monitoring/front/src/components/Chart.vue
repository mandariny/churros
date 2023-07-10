<template>
  <Bar
    id="my-chart-id"
    :options="chartOptions"
    :data="chartData"
  />
</template>

<script>
import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

export default {
  name: 'BarChart',
  components: { Bar },
  props:{
    news: {
        type: Array
    },
    info: {
      type: Number
    }
  },
  data() {
    return {
      color: ['#B0DAFF', '#FFB4B4'],
      text: ['수집 성공 건수', '수집 실패 건수'],
      chartData: {
        labels: [ "정치", "경제", "사회", "생활/문화", "IT/과학", "연예" ],
        datasets: [ { 
            label: '데이터 건수',
            backgroundColor: '',
            data: this.news 
        } ]
      },
      chartOptions: {
        responsive: false,
        maintainAspectRatio: false,
      }
    }
  },
  watch: {
    news(newValue){
        console.log(`newValue : ${newValue}`)
        this.chartData = Object.assign({}, this.chartData, {
            datasets: [{
                label: this.text[this.info],
                backgroundColor: this.color[this.info],
                data: newValue
            }]
        });
    }
  }
}
</script>