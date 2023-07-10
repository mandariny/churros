require('dotenv').config();
const express = require('express');
const connect = require('../db/index');
const Log = require('../db/logSchema');
const NewsData = require('../db/dataSchema');

const router = express.Router();

const category = ['정치', '경제', '사회', '생활/문화', 'IT/과학', '연예'];
const sub_category = {
  '정치' : ['대통령실', '국회/정당', '북한', '행정', '국방/외교', '정치일반'],
  '경제' : ['금융', '증권', '산업/재계', '증기/벤처', '부동산', '글로벌 경제', '생활경제', '경제 일반'],
  '사회' : ['사건사고', '교육', '노동', '언론', '환경', '지역', '인물', '사회 일반'],
  '생활/문화' : ['생활문화 일반'],
  'IT/과학' : ['모바일', '인터넷/SNS', '통신/뉴미디어', 'IT 일반', '보안/해킹', '컴퓨터', '게임/리뷰', '과학 일반'],
  '연예' : ['연예가화제', '방송/TV', '드라마', '뮤직', '해외연예']
}

connect();

router.get('/database/crawling', async (req, res) => {
  const start_day = req.query.start_day;
  const end_day = req.query.end_day;
  try {
    let all_result = {};
    let result = {};
    let result2 = {};
    let total1 = 0;
    let total2 = 0;
    for(let i=0; i<category.length; i++){
      const arr = await Log.find({ date_time: { $gte: new Date(start_day), $lte: new Date(end_day) }, state: 'success', category: category[i]});
      let init = 0;
      const sum = arr.reduce((acc, cur) => acc + cur.cnt, init);
      result[category[i]] = sum;
      total1 += sum;

      const arr2 = await Log.find({ date_time: { $gte: new Date(start_day), $lte: new Date(end_day) }, state: 'fail', category: category[i]});
      let init2 = 0;
      const sum2 = arr2.reduce((acc, cur) => acc + cur.cnt, init2);
      result2[category[i]] = sum2;
      total2 += sum2;
    }
    all_result['success_total'] = total1;
    all_result['fail_total'] = total2;
    all_result['success'] = result;
    all_result['fail'] = result2;

    res.json(all_result);
  } catch (err) {
    console.error(err);
    res.status(500).send('Internal Server Error');
  }
});

router.get('/database/error', async(req, res) => {
  const start_day = req.query.start_day;
  const end_day = req.query.end_day;
  const conditions = req.query.conditions;

  try {
    const result = await Log.find({ date_time: { $gte: new Date(start_day), $lte: new Date(end_day) }, level: 'ERROR', state: {$in: conditions}})
    .select('date_time level state category subcategory error_msg error_msg2');

    res.json(result);
  } catch (err) {
    console.error(err);
    res.status(500).send('Internal Server Error');
  }
});

router.get('/database/dataset', async(req, res) => {
  try {
    let result = {};
    let all_result = {};
    let total = 0;
    for(let i=0; i<category.length; i++){
      const cnt = await NewsData.find({ cat1: category[i]}).count();
      result[category[i]] = cnt;
      total += cnt;
    }
    all_result['result'] = result;
    all_result['total'] = total;
    res.json(all_result);
  } catch (err) {
    console.error(err);
    res.status(500).send('Internal Server Error');
  }
});

module.exports = router;