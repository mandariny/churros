import React from "react";
import { ReactDOM, useState } from "react";
import { Navigate, Outlet } from "react-router-dom";
import { useRecoilValue } from "recoil";
import Section from "../components/section/Section";
import Sidebar from "../components/sidebar/Sidebar";
import Topbar from "../components/topbar/Topbar";
import SurveyModal from "../modal/SurveyModal";
import SearchModal from "../modal/SearchModal";
import { accessTokenState } from "../store/auth";
import { userInfoState } from "../store/user";

const Home = () => {
  const accessToken = useRecoilValue(accessTokenState);
  const [searchOn, setSearchOn] = useState(false);
  // console.log(accessToken);

  const userInfo = useRecoilValue(userInfoState);
  // Todo: access token validation
  // if (!isAuthenticated) return <Navigate to="/landing" />;
  // 검색 모달 작동 기능
  const searchOnClick = (e) => {
    setSearchOn(true);
  };
  const searchOffClick = (e) => {
    setSearchOn(false);
  };
  
  // LocalStorage에 액세스 토큰이 없을 때만 landing으로 돌아간다
  if(!JSON.parse(localStorage.getItem("recoil-persist"))?.accessToken) return <Navigate to={"/landing"}/>

  return (
    <div className="w-screen h-screen flex flex-row justify-start bg-stone-50">
      {!userInfo?.activate && <SurveyModal />}
            {searchOn && (
        <SearchModal
          searchOnClick={searchOnClick}
          searchOffClick={searchOffClick}
        />
      )}
      <Sidebar />
      <div className="flex flex-1 flex-col justify-start w-full h-full overflow-auto">
        <Topbar searchOnClick={searchOnClick} />
        <Section>
          <Outlet />
        </Section>
      </div>
    </div>
  );
};

export default Home;
