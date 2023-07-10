import React from "react";
import { useState, useEffect } from "react";
import HoverBox from "../common/HoverBox";
import ArticleCloseButton from "./ArticleCloseButton";
import ArticleDetailModal from "../../modal/ArticleDetailModal";
import { api } from "../../axios-instance/api";

const Article = ({ shape, articleIdx, likelist }) => {
  let sizename =
    "max-w w-90 lg:flex place-content-center rounded-tl-xl rounded-bl-xl";
  // console.log(articleIdx);
  if (shape === "1") {
    sizename += " h-60";
  } else {
    sizename += " h-full";
  }
  let picturename = "";
  if (shape === "3") {
    picturename =
      "2xl:h-auto 2xl:w-48 cursor-pointer flex-none bg-cover rounded-t  text-center overflow-hidden rounded-tl-xl rounded-bl-xl";
  } else
    picturename =
      "lg:h-auto lg:w-48 cursor-pointer flex-none bg-cover rounded-t  text-center overflow-hidden rounded-tl-xl rounded-bl-xl";
  // article content
  const [content, setContent] = useState({});
  const [showDetail, setShowDetail] = useState(false);
  const [url, setUrl] = useState("");
  const [imgstate, setImgstate] = useState(true);
  const detailOnClick = () => {
    setShowDetail((before) => {
      return !before;
    });
  };
  const fetchData = async () => {
    try {
      const response = await api.get(`/news/${articleIdx}`);
      const { result, article } = response.data;
      // console.log(`loading sample article ${articleIdx}: ${result}`);
      setContent({ ...article });
      setUrl(article.url);
      setImgstate(article.imgUrl === "" ? false : true);
    } catch (error) {
      console.log(error);
    }
  };
  // console.log(content);
  useEffect(() => {
    fetchData();
  }, [articleIdx]);

  return (
    <div className={sizename}>
      <div
        className={picturename}
        style={{
          backgroundImage: `url(${content.imgUrl})`,
        }}
        title="기사 사진"
        onClick={detailOnClick}
      ></div>
      <div className={`w-4/6 border-r border-b border-l border-grey-light lg:border-l-0 lg:border-t lg:border-grey-light bg-white p-4 flex flex-col justify-between leading-normal relative rounded-br-xl rounded-tr-xl ${!imgstate ? "w-full" : ""}`}>
        <div className="mb-8 cursor-pointer" onClick={detailOnClick}>
          <ArticleCloseButton articleIdx={articleIdx} />
          <div className={`text-black font-bold mb-2 ${shape==="3" ? "text-lg line-clamp-3" : "text-xl"}`}>
            {content.title}
          </div>
          <p
            className={`text-grey-darker text-base line-clamp ${
              shape === "3" ? "line-clamp-3" : "line-clamp-5"
            }`}
          >
            {content.description}
          </p>
          <HoverBox articleIdx={articleIdx} likelist={likelist} />
        </div>
      </div>
      {showDetail && (
        <ArticleDetailModal
          url={url}
          hideDetail={detailOnClick}
          articleIdx={articleIdx}
        />
      )}
    </div>
  );
};

export default Article;
