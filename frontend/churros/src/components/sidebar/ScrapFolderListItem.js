// constants
import { SIDEBAR_ITEM_SIZE } from "../../constants/sidebar-constants";
// icons
import { IoEllipsisHorizontalSharp } from "react-icons/io5";
import { FaMinus } from "react-icons/fa";
// components
import SidebarNavLink from "./SidebarNavLink";
import { useEffect, useState } from "react";
import ScrapFolderEditModal from "../../modal/ScrapFolderEditModal";

const ScrapFolderListItem = ({ title, folderIdx }) => {
  const [modalOpen, setModalOpen] = useState(false);
  const [modalPosition, setModalPosition] = useState({ x: 0, y: 0 });

  const showEditForm = (e) => {
    e.preventDefault();
    e.stopPropagation();

    const buttonRect = e.target.getBoundingClientRect();
    setModalPosition({
      x: buttonRect.left + buttonRect.width,
      y: buttonRect.top,
    });
    setModalOpen(true);
  };

  const hideEditForm = (e) => {
    setModalOpen(false);
  };

  const navigateTo = `/scraps/${folderIdx}`;

  return (
    <SidebarNavLink
      navigateTo={navigateTo}
      className={`${SIDEBAR_ITEM_SIZE.sm}`}
    >
      {modalOpen && (
        <ScrapFolderEditModal
          position={modalPosition}
          folderIdx={folderIdx}
          onClose={hideEditForm}
        />
      )}
      <div className="w-full h-full flex flex-row justify-between items-center">
        <div
          className={`flex flex-row justify-start items-center w-full h-full hover:bg-stone-300`}
        >
          <div className="block w-7" />
          <div className="flex flex-row justify-start items-center">
            <FaMinus className="p-2" size={25} style={{ color: "837F79" }} />
            <p className="text-sm text-center mx-2">{title}</p>
          </div>
        </div>
        <IoEllipsisHorizontalSharp
          className="w-10 h-full p-2 hover:bg-stone-300"
          style={{ color: "837F79" }}
          onClick={showEditForm}
        />
      </div>
    </SidebarNavLink>
  );
};

export default ScrapFolderListItem;
