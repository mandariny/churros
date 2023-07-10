import React from "react";
import { useState, useEffect, useRef, useCallback } from "react";
import { useLocation } from "react-router-dom";
import Article from "../components/article/Article";
import useFetch from "../components/useFetch";
import { api } from "../axios-instance/api";
const SearchPage = () => {
  const location = useLocation();
  const searchData = location.state;
  const [pageNum, setPageNum] = useState(0);
  const { loading, searchList, last } = useFetch(searchData, pageNum);
  const loader = useRef();
  // 시작과 함께 axios 통신으로 리스트 받아옴
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
  const handleObserver = useCallback((entries) => {
    const target = entries[0];
    if (!last) {
      if (target.isIntersecting) {
        setPageNum((prev) => prev + 1);
      }
    }
  }, []);
  useEffect(()=>{
    setPageNum(0);
  },[searchData])

  useEffect(() => {
    likeListGet();
    const option = {
      root: null,
      rootMargin: "20px",
      threshold: 0,
    };
    const observer = new IntersectionObserver(handleObserver, option);
    if (loader.current) observer.observe(loader.current);
  }, [handleObserver]);
  return (
    <div>
      <div className="text-2xl flex item-center">
        <p className="font-bold mx-4">{searchData}</p> 에 대한 검색 결과 입니다
      </div>
      <div className="grid grid-cols-2 gap-4 p-5">
        {/* 검색결과 */}
        {searchList &&
          searchList.slice(1).map((article, idx) => (
            <div key={idx} className="col-span-1 relative">
              <Article shape="2" articleIdx={article.idx} likelist={likeList}/>
            </div>
          ))}
        {loading && <p className="col-span-2">Loading...</p>}
        <div id="loader" ref={loader} />
      </div>
    </div>
  );
};

export default SearchPage;
