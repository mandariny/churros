import React, { useState, useEffect, Fragment } from "react";
import ReactDOM from "react-dom";
import { IoAddOutline } from "react-icons/io5";
import CloseButton from "../components/common/CloseButton";
import { api } from "../axios-instance/api";
import { useSetRecoilState } from "recoil";
import { scrapFolderListState } from "../store/sidebar-global-state";

const ScrapFolderAddFormBackdrop = ({ onClose }) => {
  return (
    <div
      className="fixed top-0 left-0 w-full h-screen z-30"
      onClick={onClose}
    />
  );
};

const ScrapFolderAddFormContent = ({ position, onClose }) => {
  const setScrapFolderList = useSetRecoilState(scrapFolderListState);
  const [folderName, setFolderName] = useState("");
  const [formValid, setFormValid] = useState(false);

  const handleInputChange = (e) => {
    setFolderName(e.target.value);
  };

  const createNewScrapFolder = async (folderName) => {
    try {
      const r = await api.post("/scrap/book", {
        folderName: folderName,
      });
      // console.log(r);
      const { folderIdx } = r.data;
      setScrapFolderList((prev) => [
        ...prev,
        {
          folderIdx: folderIdx,
          folderName: folderName,
        },
      ]);
    } catch (error) {
      console.log(error);
    } finally {
      setFolderName("");
    }
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    createNewScrapFolder(folderName);
    onClose();
  };

  useEffect(() => {
    const folderNameLength = folderName.trim().length;
    if (folderNameLength === 0 || folderNameLength > 20) setFormValid(false);
    else setFormValid(true);
  }, [folderName]);

  return (
    <form
      className="absolute flex flex-row justify-between items-center w-64 h-16 p-1 z-60 bg-white drop-shadow-lg rounded-lg"
      style={{ left: `${position.x}px`, top: `${position.y}px` }}
    >
      <div className="p-1 mr-1 flex-1 h-5/6">
        <input
          className="placeholder:italic placeholder:text-slate-400 block bg-white w-full h-full border border-slate-300 rounded-md pr-3 shadow-lg focus:outline-none focus:border-orange-500 focus:ring-orange-500 focus:ring-1 sm:text-sm"
          placeholder="새 스크랩 폴더 제목..."
          type="text"
          onChange={handleInputChange}
          onClick={(e) => e.stopPropagation()}
        />
      </div>
      <button
        className={`mr-1 p-1 rounded-lg ${
          formValid
            ? "bg-stone-300 hover:bg-stone-500"
            : "bg-stone-200 pointer-events-none"
        }`}
        onClick={handleSubmit}
      >
        <IoAddOutline
          size={25}
          className={`${formValid ? "text-black" : "text-gray-100"}`}
        />
      </button>
    </form>
  );
};

const ScrapFolderAddModal = ({ position, onClose }) => {
  return (
    <Fragment>
      {ReactDOM.createPortal(
        <ScrapFolderAddFormBackdrop onClose={onClose} />,
        document.getElementById("backdrop-root")
      )}
      {ReactDOM.createPortal(
        <ScrapFolderAddFormContent position={position} onClose={onClose} />,
        document.getElementById("overlay-root")
      )}
    </Fragment>
  );
};

export default ScrapFolderAddModal;
