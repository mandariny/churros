import { useState, useEffect } from "react";
import { api, test } from "../../axios-instance/api";

const SampleArticle = ({ articleId }) => {
  const [content, setContent] = useState({});

  const fetchData = async () => {
    try {
      const response = await api.get(`/news/${articleId}`);
      const { result, article } = response.data;
      // console.log(`loading sample article ${articleId}: ${result}`);
      setContent({ ...article });
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <div className="flex flex-col w-full h-full justify-center items-center p-2">
      <div className="flex w-40 h-40 m-2 justify-center items-center">
        <img src={content?.imgUrl} className="h-full w-auto object-cover" />
      </div>
      <div className="flex flex-col flex-1 justify-center items-center p-2">
        <p
          className="line-clamp-2 text-lg font-bold"
          style={{ fontFamily: "Noto Sans KR", fontWeight: 500 }}
        >
          {content?.title}
        </p>
      </div>
    </div>
  );
};

export default SampleArticle;
