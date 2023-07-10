const mongoose = require('mongoose');

const dataSchema = new mongoose.Schema({
  _idx: {
    type: Number,
    required: true,
  },
  cat1: {
    type: String,
    required: true,
  },
  cat2: {
    type: String,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
  },
  press: {
    type: String,
  },
  link: {
    type: String,
  },
  publish_date: {
    type: Date,
    required: true,
  },
  full_text: {
    type: String,
  },
  img_src: {
    type: String,
  },
}, {collection: 'newsCol'});

module.exports = mongoose.model('NewsData', dataSchema);