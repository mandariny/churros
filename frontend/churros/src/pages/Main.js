import { useState, useEffect } from "react";
import A1Carousel from "../components/article/A1Carousel";
import { api } from "../axios-instance/api";
import Article from "../components/article/Article";
import { useRecoilValue, useResetRecoilState } from "recoil";
import { userInfoState } from "../store/user";
import { accessTokenState, refreshTokenState } from "../store/auth";
import { scrapFolderListState, showScrapFolderListState } from "../store/sidebar-global-state";

const Main = () => {
  const userInfo = useRecoilValue(userInfoState);
  const subArraySizes = [4, 2, 2, 4];
  const [carouselArticles, setCarouselArticles] = useState([]);
  const [firstRowArticles, setFirstRowArticles] = useState([]);
  const [secondRowArticles, setSecondRowArticles] = useState([]);
  const [thirdRowArticles, setThirdRowArticles] = useState([]);
  const [likeList, setLikeList] = useState([]);

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
  // userInfo가 초기화 되고 userInfo.activate가 true가 될 때 데이터를 fetch 해야한다
  const likeListGet = async () => {
    try {
      const response = await api.get(`/news/like`);
      const { result, articles } = response.data;
      setLikeList(articles);
      // console.log(`likes list set ${likeList}: ${result}`);
    } catch (error) {
      console.log(error);
      if(error.response && (error.response.status === 401 || error.response.status === 403)){
        // console.log("User not authorized");
        logout();
      }
    }
  };
  const fetchMainPageArticles = async () => {
    try {
      const res = await api.get("/news");
      // console.log(res.data);

      const { result, articles } = res.data;
      // console.log("main", result);
      // console.log("main", articles);

      let startIdx = 0;
      setCarouselArticles(
        articles.slice(startIdx, startIdx + subArraySizes[0])
      );

      startIdx += subArraySizes[0];
      setFirstRowArticles(
        articles.slice(startIdx, startIdx + subArraySizes[1])
      );

      startIdx += subArraySizes[1];
      setSecondRowArticles(
        articles.slice(startIdx, startIdx + subArraySizes[2])
      );

      startIdx += subArraySizes[2];
      setThirdRowArticles(
        articles.slice(startIdx, startIdx + subArraySizes[3])
      );
    } catch (error) {
      if(error.response && (error.response.status === 401 || error.response.status === 403)){
        console.log("User not authorized");
        logout();
      }
    }
  };

  useEffect(() => {
    if (userInfo?.activate) {
      fetchMainPageArticles();
      likeListGet();
    }
  }, [userInfo]);

  return (
    <div className="flex-1 flex-col justify-start w-full h-full p-2 overflow-y-auto">
      <div className="block w-full h-[34rem] mb-4 shadow-lg">
        <A1Carousel slides={carouselArticles}  likelist={likeList} />
      </div>
      <div className="grid grid-cols-4 gap-4 w-full mb-2">
        {firstRowArticles?.map((articleIdx, idx) => (
          <div className="col-span-2 h-64">
            <Article key={idx} shape="2" articleIdx={articleIdx} likelist={likeList}/>
          </div>
        ))}
      </div>
      <hr className="bg-black opacity-20 h-1 mb-2" />
      <div className="grid grid-cols-4 gap-4 w-full mb-2">
        {secondRowArticles?.map((articleIdx, idx) => (
          <div className="col-span-2 h-64">
            <Article key={idx} shape="2" articleIdx={articleIdx}  likelist={likeList}/>
          </div>
        ))}
      </div>
      <hr className="bg-black opacity-20 h-1 mb-2" />
      <div className="grid grid-cols-4 gap-4 w-full">
        {thirdRowArticles?.map((articleIdx, idx) => (
          <div className="col-span-1 h-64">
            <Article key={idx} shape="3" articleIdx={articleIdx}  likelist={likeList}/>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Main;
