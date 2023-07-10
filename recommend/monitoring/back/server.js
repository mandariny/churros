require('dotenv').config({path: '../.env'});
const express = require('express');
const analytics = require('./routers/analytics');
const app = express();
const { PORT } = process.env;
const cors = require("cors");
app.use(
    cors({
      origin: true,
    })
  );
app.use(analytics);

app.use('/', analytics);

app.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}`);
});
