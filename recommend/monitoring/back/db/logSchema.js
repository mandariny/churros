const mongoose = require('mongoose');

const logSchema = new mongoose.Schema({
  date_time: {
    type: Date,
    required: true,
  },
  level: {
    type: String,
    required: true,
  },
  state: {
    type: String,
  },
  category: {
    type: String,
  },
  sub_category: {
    type: String,
  },
  running_time: {
    type: Number,
  },
  cnt: {
    type: Number,
  },
  error_msg: {
    type: String,
  },
  error_msg2: {
    type: String,
  },
}, {collection: 'crawlingLog'});

module.exports = mongoose.model('Log', logSchema);