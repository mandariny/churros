import React, { useEffect, useState } from "react";
import { Fragment } from "react";
import ReactDOM from "react-dom";
import { IoCheckbox, IoSquareOutline, IoAddOutline } from "react-icons/io5";
import { api } from "../axios-instance/api";
import { useRecoilState, useRecoilValue, useSetRecoilState } from "recoil";
import { scrapFolderListState } from "../store/sidebar-global-state";
import CloseButton from "../components/common/CloseButton";

const ScrapDialogueBackdrop = ({ onClose }) => {
  return (
    <div
      className="fixed top-0 left-0 w-full h-screen z-30 bg-black opacity-75"
      onClick={onClose}
    />
  );
};

const ScrapDialogueItem = ({
  articleId,
  folderIdx,
  folderName,
  isScrapped,
}) => {
  const [scrapped, setScrapped] = useState(isScrapped);

  const toggleCheckbox = (e) => {
    e.preventDefault();
    requestScrap(!scrapped);
    setScrapped((prev) => !prev);
  };

  // Todo: 현재 폴더에 해당 기사가 스크랩된 상태가 변하면 api 요청 보내기
  const requestScrap = async (isScrapped) => {
    try {
      const response = await api.put("/scrap/article", {
        articleIdx: articleId,
        folderIdx: folderIdx,
        folderName: folderName,
        scrapped: isScrapped
      });
      // console.log(response);
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div className="flex flex-row w-full h-max my-2 px-4">
      <div onClick={toggleCheckbox}>
        {scrapped ? (
          <IoCheckbox size={25} className="text-blue-800" />
        ) : (
          <IoSquareOutline size={25} />
        )}
      </div>
      <p
        className="text-base font-bold px-4"
        style={{ fontFamily: "Noto Sans KR", fontWeight: 500 }}
      >
        {folderName}
      </p>
    </div>
  );
};

const ScrapDialogueList = ({ items }) => {
  // console.log(items);
  return (
    <div className="flex flex-col flex-1 overflow-y-auto">
      {items.map(({ articleId, folderIdx, folderName, isScrapped }) => {
        return (
          <ScrapDialogueItem
            articleId={articleId}
            folderIdx={folderIdx}
            folderName={folderName}
            isScrapped={isScrapped}
          />
        );
      })}
    </div>
  );
};

const ScrapFolderAddForm = ({ articleId, onFormClose, onDialogueClose }) => {
  const setScrapFolderList = useSetRecoilState(scrapFolderListState);

  const [folderName, setFolderName] = useState("");
  const [formValid, setFormValid] = useState(false);

  const handleInputChange = (e) => {
    setFolderName(e.target.value);
  };

  const createNewScrapFolderAndAddArticle = async (folderName) => {
    try {
      const r = await api.post("/scrap/book", {
        folderName: folderName,
      });
      // console.log(r);
      const { folderIdx } = r.data;

      setScrapFolderList((prev) => [
        ...prev,
        { folderIdx: folderIdx, folderName: folderName },
      ]);

      const s = await api.put("/scrap/article", {
        articleIdx: articleId,
        folderIdx: folderIdx,
        folderName: folderName,
        scrapped: true,
      });
      // console.log(s);
    } catch (error) {
      console.log(error);
    } finally {
      setFolderName("");
      onFormClose();
      onDialogueClose();
    }
  };

  useEffect(() => {
    const folderNameLength = folderName.trim().length;

    if (folderNameLength === 0 || folderNameLength > 20) setFormValid(false);
    else setFormValid(true);
  }, [folderName]);

  return (
    <div className="absolute top-0 left-0 flex flex-row justify-between items-center w-full h-full p-1 z-60 bg-stone-200 rounded-lg">
      <CloseButton
        className="absolute top-0 left-0 -translate-x-1/3 -translate-y-1/3"
        onClose={() => {
          setFolderName("");
          onFormClose();
        }}
      />
      <div className="p-1 mr-1 flex-1 h-5/6">
        <input
          className="placeholder:italic placeholder:text-slate-400 block bg-white w-full h-full border border-slate-300 rounded-md pr-3 shadow-sm focus:outline-none focus:border-sky-500 focus:ring-sky-500 focus:ring-1 sm:text-sm"
          placeholder="새 스크랩 폴더 제목..."
          type="text"
          onChange={handleInputChange}
          onClick={(e) => e.stopPropagation()}
        />
      </div>
      <div
        className={`mr-1 p-1 rounded-lg ${
          formValid
            ? "bg-stone-300 hover:bg-stone-500"
            : "bg-stone-200 pointer-events-none"
        }`}
        onClick={() => createNewScrapFolderAndAddArticle(folderName)}
      >
        <IoAddOutline
          size={25}
          className={`${formValid ? "text-black" : "text-gray-100"}`}
        />
      </div>
    </div>
  );
};

const ScrapDialogueContent = ({ articleId, onClose }) => {
  const [isFormOpen, setFormOpen] = useState(false);
  const [scrapDialogueItems, setScrapDialogueItems] = useState([]);

  const fetchData = async () => {
    try {
      const response = await api.get("/scrap/folders", {
        params: {
          articleIdx: articleId,
        },
      });
      // console.log(response);

      const { folder } = response.data;
      setScrapDialogueItems(
        folder.map((f) => ({
          articleId: articleId,
          folderIdx: f.folderIdx,
          folderName: f.folderName,
          isScrapped: f.scrapped,
        }))
      );
    } catch (error) {
      console.log(error);
    } finally {
      console.log(scrapDialogueItems);
    }
  };

  const openForm = (e) => {
    setFormOpen(true);
  };

  const closeForm = (e) => {
    setFormOpen(false);
  };

  // fetch data
  useEffect(() => {
    fetchData();
  }, []);

  return (
    <div className="fixed flex flex-col w-72 h-auto rounded-lg p-2 bg-white top-1/2 left-1/2 -translate-y-1/2 -translate-x-1/2 z-50">
      <div className="flex w-full h-10 justify-start items-center mb-2 px-2">
        <p
          className="text-xl"
          style={{ fontFamily: "Noto Sans KR", fontWeight: 500 }}
        >
          스크랩하기
        </p>
      </div>
      <ScrapDialogueList items={scrapDialogueItems} />
      <div className="relative w-full h-14 p-2 mt-2">
        <div
          className="flex flex-row justify-start items-center w-full h-full"
          onClick={openForm}
        >
          <IoAddOutline size={25} />
          <p
            className="mx-2"
            style={{ fontFamily: "Noto Sans KR", fontWeight: 500 }}
          >
            새 스크랩 폴더 만들기
          </p>
        </div>
        {isFormOpen && (
          <ScrapFolderAddForm
            articleId={articleId}
            onFormClose={closeForm}
            onDialogueClose={onClose}
          />
        )}
      </div>
    </div>
  );
};

const ScrapDialogueModal = ({ target, onClose }) => {
  return (
    <Fragment>
      {ReactDOM.createPortal(
        <ScrapDialogueBackdrop onClose={onClose} />,
        document.getElementById("backdrop-root")
      )}
      {ReactDOM.createPortal(
        <ScrapDialogueContent articleId={target} onClose={onClose} />,
        document.getElementById("overlay-root")
      )}
    </Fragment>
  );
};

export default ScrapDialogueModal;
