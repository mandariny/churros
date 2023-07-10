import React, { useState, useEffect, Fragment } from "react";
import ReactDOM from "react-dom";
import { IoAddOutline } from "react-icons/io5";
import CloseButton from "../components/common/CloseButton";
import { api } from "../axios-instance/api";
import { useSetRecoilState } from "recoil";
import { scrapFolderListState } from "../store/sidebar-global-state";
import { IoTrash, IoCreateOutline } from "react-icons/io5";

const ScrapFolderEditFormBackdrop = ({ onClose }) => {
  return (
    <div
      className="fixed top-0 left-0 w-full h-screen z-30"
      onClick={onClose}
    />
  );
};

const ScrapFolderEditFormContent = ({ position, folderIdx, onClose }) => {
  // console.log(`ScrapFolderEditFormContent folderIdx:${folderIdx}`);
  return (
    <div
      className="absolute flex flex-col justify-start z-60 w-64 p-1 bg-white drop-shadow-lg rounded-lg"
      style={{ left: `${position.x}px`, top: `${position.y}px` }}
    >
      <div className="flex flex-row w-full h-10">
        <div className="flex flex-row justify-start items-center">
          <IoTrash className="p-2" size={35}/>
          <p className="text-sm text-center">삭제</p>
        </div>
      </div>
      <div className="flex flex-row w-full h-10">
        <div className="flex flex-row justify-start items-center">
          <IoCreateOutline
            className="p-2"
            size={35}
          />
          <p className="text-sm text-center">이름 변경</p>
        </div>
      </div>
    </div>
  );
};

const ScrapFolderEditModal = ({ position, folderIdx, onClose }) => {
  return (
    <Fragment>
      {ReactDOM.createPortal(
        <ScrapFolderEditFormBackdrop onClose={onClose} />,
        document.getElementById("backdrop-root")
      )}
      {ReactDOM.createPortal(
        <ScrapFolderEditFormContent
          position={position}
          folderIdx={folderIdx}
          onClose={onClose}
        />,
        document.getElementById("overlay-root")
      )}
    </Fragment>
  );
};

export default ScrapFolderEditModal;
