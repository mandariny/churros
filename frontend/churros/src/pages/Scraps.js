import React from "react";
import { useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import Article from "../components/article/Article";
import { api } from "../axios-instance/api";
import { Fragment } from "react";
import EmptyPage from "../components/common/EmptyPage";
import { useRecoilValue, useResetRecoilState } from "recoil";
import { userInfoState } from "../store/user";
import { accessTokenState, refreshTokenState } from "../store/auth";
import { scrapFolderListState, showScrapFolderListState } from "../store/sidebar-global-state";

const ScrapsPage = (props) => {
  const { idx } = useParams();

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

  const [articleList, setArticleList] = useState([]);
  const [likeList, setLikeList] = useState([]);
  const likeListGet = async () => {
    try {
      const response = await api.get(`/news/like`);
      const { result, articles } = response.data;
      setLikeList(articles);
      // console.log(`likes list set ${likeList}: ${result}`);
    } catch (error) {
      console.log(error);
    }
  };
  const scrapListGet = async (scrapFolderIdx) => {
    try {
      const response = await api.get(`/scrap/${scrapFolderIdx}`);
      const { result, empty, articles } = response.data;
      
      if(empty){
        setArticleList([]);
      }
      else{
        setArticleList(articles);
      }

      // console.log(`scrap list set ${articles}: ${result}`);
    } catch (error) {
      console.log(error);
      if(error.response && (error.response.status === 401 || error.response.status === 403)){
        console.log("User not authorized");
        logout();
      }
    }
  };
  useEffect(() => {
    // 시작과 함께 axios 통신으로 리스트 받아옴
    scrapListGet(idx);
    likeListGet();
  }, [idx]);
  return (
    <Fragment>
      {articleList.length === 0 ? (
        <div className="flex flex-col w-full h-full justify-center items-center">
          <EmptyPage
            message={"스크랩한 기사가 없습니다"}
            className={"w-full h-full"}
          />
        </div>
      ) : (
        <div className="grid grid-cols-2 gap-4 p-5">
          <div className="col-span-full place-content-center">
            <Article shape="1" articleIdx={articleList[0]}  likelist={likeList}/>
          </div>
          {articleList &&
            articleList.length > 0 &&
            articleList.slice(1).map((article, idx) => (
              <div key={idx} className="col-span-1">
                <Article shape="2" articleIdx={article}  likelist={likeList}/>
              </div>
            ))}
        </div>
      )}
    </Fragment>
  );
};

export default ScrapsPage;
