import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
// import { useLocation } from "react-router-dom";
// import { useEffect } from "react";
import Landing from "./pages/Landing";
import Home from "./pages/Home";
import Main from "./pages/Main";
import Likes from "./pages/Likes";
import Search from "./pages/Search";
import Scraps from "./pages/Scraps";
import KakaoRedirectHandler from "./pages/KakaoRedirectHandler";
// // 6팀 잠시 요청
// import TagManager from "./module";

// const tagManager = new TagManager(
//   "http://ec2-3-38-85-143.ap-northeast-2.compute.amazonaws.com/api/v1/dump",
//   "3ee56d05-9119-41dd-b2a3-5325ef06a713",
//   ["click"],
//   "*"
// );

function App() {
  // // 6팀 잠시 요청
  // const location = useLocation();
  // useEffect(() => {
  //   tagManager.attach();
  //   return () => {
  //     tagManager.detach();
  //   };
  // }, [location]);

  return (
    <Router>
      <Routes>
        <Route path="/landing" element={<Landing />} />
        <Route path="/" element={<Home />}>
          <Route index element={<Main />} />
          <Route path="likes" element={<Likes />} />
          <Route path="scraps/:idx" element={<Scraps />} />
          <Route path="search" element={<Search />} />
        </Route>
        <Route path="/kakao/handler" element={<KakaoRedirectHandler />} />
      </Routes>
    </Router>
  );
}

export default App;
