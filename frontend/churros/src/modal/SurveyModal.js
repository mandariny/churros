import React, { useState, useEffect } from "react";
import ReactDOM from "react-dom";

import { Fragment } from "react";
import { useSetRecoilState, useResetRecoilState } from "recoil";
import SampleArticle from "../components/article/SampleArticle";
import { IoCheckbox } from "react-icons/io5";
import { api } from "../axios-instance/api";
import { userInfoState } from "../store/user";
import { accessTokenState, refreshTokenState } from "../store/auth";
import { showScrapFolderListState, scrapFolderListState } from "../store/sidebar-global-state";

const SurveyBackdrop = () => {
  return (
    <div className="fixed top-0 left-0 w-full h-screen z-30 bg-black opacity-75" />
  );
};

const SelectionHighlighter = () => {
  return (
    <Fragment>
      <div className="absolute top-0 left-0 w-full h-full z-20 bg-black opacity-50 rounded-lg" />
      <IoCheckbox
        size={50}
        className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 text-green-500 z-30"
      />
    </Fragment>
  );
};

const SurveySubmitButton = ({ onClick, isActive }) => {
  return (
    <button
      onClick={onClick}
      className={`w-32 h-16 text-xl rounded-2xl ${
        !isActive
          ? "text-gray-500 bg-stone-300 pointer-events-none"
          : "text-white bg-orange-400"
      }`}
      style={{ fontFamily: "Noto Sans KR", fontWeight: 500 }}
    >
      완료
    </button>
  );
};

const SurveyContent = () => {
  // Recoil state
  const setUserInfo = useSetRecoilState(userInfoState);
  const resetAccessToken = useResetRecoilState(accessTokenState);
  const resetUserInfo = useResetRecoilState(userInfoState);
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

  // state
  const [isOpen, setIsOpen] = useState(false);
  const [sampleArticles, setSampleArticles] = useState([]);
  const [submitButtonActive, setSubmitButtonActive] = useState(false);

  const fetchSampleArticles = async () => {
    try {
      const res = await api.get("/news/sample");
      const { result, articles } = res.data;

      // console.log(result);
      // console.log(artiscles);

      setSampleArticles(
        articles.map((id, idx) => ({
          index: idx,
          articleId: id,
          selected: false,
        }))
      );
    } catch (error) {
      console.log(error);

      if(error.response && (error.response.status === 401 || error.response.status === 403)){
        logout();
      }
    }
  };
  const handleSubmit = (event) => {
    event.preventDefault();

    // send user interests info to server
    const selectedArticles = sampleArticles.filter(({ selected }) => selected);
    selectedArticles.forEach(async (item) => {
      try {
        // console.log(item);

        const r = await api.put("/news/read", {
          articleId: item.articleId,
        });

        const { result } = r.data;
        // console.log(result);

        // user 활성화 요청
        const s = await api.post("/user/activate");
        // console.log(s);
        
      } catch (error) {
        console.log(error);
      }
    });

    setIsOpen(false);

    // 모달 transition이 종료된 후 userInfo를 갱신하여 Survey 모달을 언마운트한다
    setTimeout(() => {
      setUserInfo((prev) => ({
        ...prev,
        activate: true,
      }));
    }, 600);
  };

  // 컴포넌트 마운트 시 isOpen 상태를 변경시킨다
  // 모달 창 Slide Up transition 실행된다
  useEffect(() => {
    // fetchDummySampleArticles();
    fetchSampleArticles();
  }, []);

  // 샘플 기사가 변경될 때마다 SubmitButtonActive 상태가 갱신된다
  useEffect(() => {
    setIsOpen(true);
    // console.log(sampleArticles);
    const count = sampleArticles.filter(({ selected }) => selected).length;

    if (count >= 2) setSubmitButtonActive(true);
    else setSubmitButtonActive(false);
  }, [sampleArticles]);

  const updateSelectedSampleArticles = (idx) => {
    setSampleArticles((prev) => {
      return prev.map((item) => {
        if (item.index !== idx) return item;
        return { ...item, selected: !item.selected };
      });
    });
  };

  return (
    <div
      className={`fixed w-1/2 h-auto z-50 top-1/2 left-1/2 -translate-x-1/2 ${
        isOpen ? "-translate-y-1/2" : "translate-y-[50vh]"
      } transition delay-300 ease-in-out`}
    >
      <div className="flex flex-col w-full h-full justify-start bg-white rounded-2xl">
        <header className="inline-block w-full h-fit p-4">
          <p
            className="text-4xl p-4"
            style={{ fontFamily: "Noto Sans KR", fontWeight: 500 }}
          >
            관심있는 기사를 모두 선택해주세요
          </p>
        </header>
        <section className="flex-1 grid grid-cols-3 gap-4 px-8 py-4">
          {sampleArticles.map(({ index, articleId, selected }) => (
            <div
              key={index}
              onClick={() => updateSelectedSampleArticles(index)}
              className="relative"
            >
              {selected && <SelectionHighlighter />}
              <SampleArticle articleId={articleId} />
            </div>
          ))}
        </section>
        <footer className="flex flex-row justify-end items-center p-4">
          <SurveySubmitButton
            onClick={handleSubmit}
            isActive={submitButtonActive}
          />
        </footer>
      </div>
    </div>
  );
};

const SurveyModal = () => {
  return (
    <Fragment>
      {ReactDOM.createPortal(
        <SurveyBackdrop />,
        document.getElementById("backdrop-root")
      )}
      {ReactDOM.createPortal(
        <SurveyContent />,
        document.getElementById("overlay-root")
      )}
    </Fragment>
  );
};

export default SurveyModal;
