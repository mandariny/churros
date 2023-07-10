<template>
  <div id="id">
    <label for="datepicker_start">검색 시작일</label>
    <b-form-datepicker id="datepicker_start" v-model="start_day" :max="end_day" class="my-2"></b-form-datepicker>
    <label for="datepicker_end">검색 종료일</label>
    <b-form-datepicker id="datepicker_end" v-model="end_day" :min="start_day" class="my-2"></b-form-datepicker>
    
    <b-form-group v-slot="{ ariaDescribedby }"> 
      <b-form-checkbox-group
        id="checkbox-group"
        v-model="selected"
        :options="options"
        :aria-describedby="ariaDescribedby"
        name="flavour"
        :size="size"
        class="mb-1 d-flex"
      ></b-form-checkbox-group>
    </b-form-group>
    
    <b-button variant="outline-secondary" @click="getData" class="my-2">조회하기</b-button>

    <b-form-textarea
      id="textarea"
      v-model="result"
      placeholder="확인하고 싶은 에러를 선택해주세요"
      rows="3"
      max-rows="20"
    ></b-form-textarea>
  </div>
</template>

<script>
import {api} from "../utils/axios"

export default {
    components: {
    },
    data() {
      const now = new Date();
      return {
        start_day: `${now.getFullYear()}-${now.getMonth() + 1}-${now.getDate()}`,
        end_day: `${now.getFullYear()}-${now.getMonth() + 1}-${now.getDate()}`,
        selected: [], 
        options: [
          { text: 'CRAWLING', value: 'CRAWLING' },
          { text: 'DB', value: 'DB' },
          { text: 'ETC', value: 'ETC' },
        ],
        result: '',
        size: 'lg' 
      }
    },
    methods:{
        getData() {
            console.log("click!")
            api.error({"start_day" : this.start_day, "end_day" : this.end_day, "conditions" : this.selected})
            .then((res) =>{
                this.result = res.data;
            });
        }
    }
  }
</script>

<style scoped>
    .line {
        padding: 0 1rem 0 0 !important;
    }
</style>