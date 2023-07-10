import React, { useState } from "react";
import { useSetRecoilState } from "recoil";
import SidebarItem from "./SidebarItem";
import {
  IoChevronForwardOutline,
  IoFolderSharp,
  IoAddOutline,
} from "react-icons/io5";
import { SIDEBAR_ITEM_SIZE } from "../../constants/sidebar-constants";
import { showScrapFolderListState } from "../../store/sidebar-global-state";
import ScrapFolderAddModal from "../../modal/ScrapFolderAddModal";
import ScrapFolderEditModal from "../../modal/ScrapFolderEditModal";

const ScrapFolderTab = () => {
  const setShowScrapFolderList = useSetRecoilState(showScrapFolderListState);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalPosition, setModalPosition] = useState({x: 0, y: 0});

  const toggleScrapFolderList = () => {
    setShowScrapFolderList((prev) => !prev);
  };

  const showScrapFolderAddModal = (e) => {
    // console.log("show scrap folder modal")
    const buttonRect = e.target.getBoundingClientRect();
    setModalPosition({x: buttonRect.left + buttonRect.width, y: buttonRect.top});
    setModalOpen(true);
  }

  const hideScrapFolderAddModal = () => {
    // console.log("hide scrap folder modal")
    setModalOpen(false);
  }

  return (
    <SidebarItem className={`${SIDEBAR_ITEM_SIZE.sm}`}>
      <div className="relative w-full h-full flex flex-row justify-between items-center">
        { modalOpen && <ScrapFolderAddModal position={modalPosition} onClose={hideScrapFolderAddModal}/>}
        <div
          className={`flex flex-row justify-start items-center w-full hover:bg-stone-300`}
          onClick={toggleScrapFolderList}
        >
          <IoChevronForwardOutline
            className="w-7 pl-2"
            size={30}
            style={{ color: "837F79" }}
          />
          <div className="flex flex-row justify-start items-center">
            <IoFolderSharp
              className="p-2"
              size={35}
              style={{ color: "FFB240" }}
            />
            <p className="text-sm text-center">스크랩 폴더</p>
          </div>
        </div>
        <IoAddOutline
          className="w-10 h-full p-2 hover:bg-stone-300"
          style={{ color: "837F79" }}
          onClick={showScrapFolderAddModal}
        />
      </div>
    </SidebarItem>
  );
};

export default ScrapFolderTab;
