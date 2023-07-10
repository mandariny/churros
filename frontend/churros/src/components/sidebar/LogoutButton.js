import React from "react";
import { IoLogOutOutline } from "react-icons/io5";
import { api } from "../../axios-instance/api";
import { useNavigate } from "react-router-dom";
import { useResetRecoilState } from "recoil";
import { userInfoState } from "../../store/user";
import { accessTokenState, refreshTokenState } from "../../store/auth";
import { showScrapFolderListState, scrapFolderListState } from "../../store/sidebar-global-state";

const LogoutButton = () => {
  const navigate = useNavigate();

  const resetUserInfo = useResetRecoilState(userInfoState);
  const resetAccessToken = useResetRecoilState(accessTokenState);
  const resetRefreshToken = useResetRecoilState(refreshTokenState);
  const resetShowScrapFolderList = useResetRecoilState(showScrapFolderListState);
  const resetScrapFolderList = useResetRecoilState(scrapFolderListState);

  const logout = () => {
    resetAccessToken();
    resetRefreshToken();
    resetUserInfo();
    resetShowScrapFolderList();
    resetScrapFolderList();
  }

  const fetchData = async () => {
    try {
      const response = await api.post(`/user/logout`);
      const { result } = response.data;
      // console.log(`logout result : ${result}`);
      // console.log("reset recoil-persist");
      logout();
      navigate("/landing")
    } catch (error) {
      console.log(error);
    }
  };
  const handleLogout = (event) => {
    event.preventDefault();
    fetchData();
  };

  return (
    <div className={"flex flex-row justify-start items-center w-full h-10"} onClick={handleLogout}>
      <IoLogOutOutline size={35} className="p-2" />
      <p className="text-sm text-center">로그아웃</p>
    </div>
  );
};

export default LogoutButton;
