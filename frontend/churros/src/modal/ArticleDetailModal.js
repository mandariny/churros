import ReactDOM from "react-dom";
import { Fragment } from "react";
import { api } from "../axios-instance/api";
import { IoClose } from "react-icons/io5";
import { useState, useEffect } from "react";

const ArticleDetailBackdrop = ({ hideDetail }) => {
  return (
    <div
      className="fixed top-0 left-0 w-full h-screen z-30"
      onClick={hideDetail}
    ></div>
  );
};

const ArticleDetailContent = ({ url, hideDetail, articleIdx }) => {
  // console.log(url);
  const [htmlObject, setHtmlObject] = useState();

  // read?oid=117&aid=0003716035
  const isEntertainArticle = url.match(/(entertain.naver.com)/);
  // console.log(isEntertainArticle);

  let articleLocation;
  let ent;
  
  if(!isEntertainArticle){
    ent = false;
    articleLocation = url.match(/(?<=article\/)(.*?)(?=\?)/);
    
  }
  else{
    ent = true;
    articleLocation = url.match(/(?<=entertain.naver.com\/)(.*)/);
  }
  
  // console.log(articleLocation);

  useEffect(() => {
    fetchData(articleLocation[0]);
  }, []);

  const fetchData = async (url) => {
    try {
      const response = await api.get(`/news/call`, {
        params: {
          url: url,
          ent: ent
        },
      });

      const htmlContent = response.data["html"];
      setHtmlObject(
        <iframe
          srcDoc={htmlContent}
          title="Article Detail"
          style={{
            width: "100%",
            height: "100%",
            border: "none",
          }}
        />
      );
      fetchReadData();
    } catch (error) {
      console.log(error);
    }
  };
  const fetchReadData = async () => {
    try {
      const response = await api.put(`/news/read`, { articleId: articleIdx });
      const { result } = response.data;
      // console.log(`Reading status updated ${result}`);
    } catch (error) {
      console.log(error);
    }
  };
  const modalHolderStyle = {
    position: "fixed",
    top: "10%",
    left: "15%",
    width: "70%",
    height: "90%",
    zIndex: 100,
    overflow: "hidden",
  };
  return (
    <>
      {htmlObject ? (
        <div className="drop-shadow-xl" style={modalHolderStyle}>
          <div className="flex flex-row justify-between bg-white">
            <div
              className="p-1 text-gray-500 rounded-lg hover:bg-red-500 transition duration-300 hover:text-white"
              onClick={hideDetail}
            >
              <IoClose size={30} />
            </div>
          </div>
          <div className="flex flex-col w-full h-full justify-start bg-white pointer-events-none">
            <section className="w-full h-full overflow-y-auto">{htmlObject}</section>
          </div>
        </div>
      ) : (
        <div></div>
      )}
    </>
  );
};

const ArticleDetailModal = ({ url, hideDetail, articleIdx }) => {
  // console.log(url);
  // console.log(typeof url);
  return (
    <Fragment>
      {ReactDOM.createPortal(
        <ArticleDetailBackdrop hideDetail={hideDetail} />,
        document.getElementById("backdrop-root")
      )}
      {ReactDOM.createPortal(
        <ArticleDetailContent
          url={url}
          hideDetail={hideDetail}
          articleIdx={articleIdx}
        />,
        document.getElementById("overlay-root")
      )}
    </Fragment>
  );
};

export default ArticleDetailModal;
