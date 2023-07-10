import ArticleCloseButton from "./ArticleCloseButton";
import HoverBox from "../common/HoverBox";
import { useState, useEffect } from "react";
import { api } from "../../axios-instance/api";
import ArticleDetailModal from "../../modal/ArticleDetailModal";

const A1 = ({ articleIdx, likelist }) => {
  const [content, setContent] = useState({});
  const [showDetail, setShowDetail] = useState(false)
  const [url, setUrl] = useState("")
  const detailOnClick=() => {
    setShowDetail((before) => {
      return !before
    })
  }
  // console.log(articleIdx);

  const fetchData = async () => {
    try {
      const response = await api.get(`/news/${articleIdx}`);
      const { result, article } = response.data;
      // console.log(`loading sample article ${articleIdx}: ${result}`);
      setContent({ ...article });
      setUrl(article.url)
    } catch (error) {
      console.log(error);
    }
  };

  // console.log(content);
  // console.log(url)
  useEffect(() => {
    fetchData();
  }, []);

  return (
    <>
      <div
        className="flex flex-col w-full h-full justify-start"
        onClick={detailOnClick}
      >
        {/* 기사 썸네일 */}
        <div className="relative w-full h-3/4 overflow-hidden cursor-pointer">
          <img
            className="absolute w-full h-auto left-1/2 transform -translate-x-1/2"
            src={content.imgUrl}
            alt="alt"
          />
          <ArticleCloseButton articleIdx={articleIdx} />
        </div>
        <div className="relative flex flex-col flex-1 justify-start items-center bg-stone-100 cursor-pointer">
          {/* 기사 타이틀 및 요약 */}
          <div className="flex flex-col w-4/5 h-full justify-center">
            <p
              className="text-5xl text-bold text-center p-1 truncate ..."
              style={{ fontFamily: "Noto Sans KR", fontWeight: 500 }}
            >
              {content.title}
            </p>
            <p
              className="text-3xl text-bold text-center p-1 truncate ..."
              style={{ fontFamily: "Noto Sans KR", fontWeight: 400 }}
            >
              {content.description}
            </p>
          </div>
          <HoverBox articleIdx={articleIdx} likelist={likelist}/>
        </div>
      </div>
      {showDetail && <ArticleDetailModal url={url} hideDetail={detailOnClick} articleIdx={articleIdx} />}
    </>
  );
};

export default A1;
