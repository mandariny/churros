require('dotenv').config({path: '../../.env'});
const mongoose = require('mongoose');

const { MONGO_URI } = process.env;

module.exports = async function connect() {
  try{
    await mongoose.connect(MONGO_URI, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    });

    console.log("몽고디비 연결 성공");
  }catch(err){
    console.log("몽고디비 연결 실패");
  }
};