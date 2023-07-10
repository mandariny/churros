/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{js, jsx, ts, tsx}"],
  theme: {
    extend: {
      fontFamily: {
        cooper: ["cooper-black-std", "serif"],
      },
      spacing: {
        "10vh": "10vh",
        "50vh": "50vh",
        "10%": "10%",
        "20%": "20%",
        "25%": "25%",
        "128": "28rem",
      },
      zIndex:{
        "60": "60",
        "70": "70",
        "80": "80",
        "90": "90",
        "100": "100",
      }
    },
  },
  plugins: [require("@tailwindcss/line-clamp")],
};
