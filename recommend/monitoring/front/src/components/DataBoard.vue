<template>
  <div id="id">
    <!-- <p>Res: {{ result }} </p> -->
    <p> 총 데이터 : {{total | numberWithCommas}} </p>
    <PieChart :dataset="result" :height="500" :width="800" class="my-5"/>
  </div>
</template>

<script>
import {api} from "../utils/axios"
import PieChart from "./PieChart"

export default {
    components: {
        PieChart
    },
    data() {
      return {
        result: {},
        total: 0,
      }
    },
    mounted(){
        this.getData();
    },
    filters: {
      numberWithCommas(value) {
        return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
      }
    },
    methods:{
        getData() {
            console.log("mounted!")
            api.dataset()
            .then((res) =>{
                this.result = res.data.result;
                this.total = res.data.total;
            });
        }
    }
  }
</script>

<style scoped>
    .row {
        display: flex;
    }
</style>