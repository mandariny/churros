// react
import React from "react";
// recoil
import { useRecoilValue, useSetRecoilState } from "recoil";
// constants
import { SIDEBAR_TAB_KEYS as keys } from "../../constants/sidebar-constants";
// global state
import {
  showScrapFolderListState,
  scrapFolderListState,
} from "../../store/sidebar-global-state";
// components
import LogoTab from "./LogoTab";
import UserProfileTab from "./UserProfileTab";
import MainArticleTab from "./MainArticleTab";
import LikedArticleTab from "./LikedArticleTab";
import ScrapFolderTab from "./ScrapFolderTab";
import ScrapFolderListItem from "./ScrapFolderListItem";
import LogoutButton from "./LogoutButton";

import { api } from "../../axios-instance/api";
import { useEffect } from "react";

const Sidebar = () => {
  const showScrapFolderList = useRecoilValue(showScrapFolderListState);
  const scrapFolderList = useRecoilValue(scrapFolderListState);
  const setScrapFolderList = useSetRecoilState(scrapFolderListState);

  const fetchScrapFolderList = async () => {
    try {
      const res = await api.get("/scrap");
      // console.log(res.data);

      const { empty, folder } = res.data;
      // console.log(`is folder empty: ${empty}`)
      // console.log(`scrap folder: ${folder}`);

      if (!empty) {
        setScrapFolderList(
          folder?.map((f) => ({
            folderIdx: f.folderIdx,
            folderName: f.folderName,
          }))
        );
      }
    } catch (error) {
      console.log(error);
    }
  };

  const fetchDummyScrapFolderList = () => {
    setScrapFolderList([
      {
        folderIdx: 1,
        folderName: "정치"
      },
      {
        folderIdx: 2,
        folderName: "IT"
      },
      {
        folderIdx: 3,
        folderName: "시사"
      },
      {
        folderIdx: 4,
        folderName: "연예"
      },
    ])
  }

  // 컴포넌트가 마운트 될 때 데이터 fetch
  useEffect(() => {
    fetchScrapFolderList();
    // fetchDummyScrapFolderList();
  }, []);

  return (
    <aside className="flex flex-col justify-start w-64 h-screen bg-stone-100">
      <LogoTab key={keys.logoTab} />

      <UserProfileTab key={keys.userProfileTab} />

      <MainArticleTab key={keys.mainArticleTab} itemId={keys.mainArticleTab} />

      <LikedArticleTab
        key={keys.likedArticleTab}
        itemId={keys.likedArticleTab}
      />

      <ScrapFolderTab key={keys.scrapFolderTab} />

      <div className="relative flex-1 overflow-y-auto">
        {showScrapFolderList &&
          scrapFolderList?.length > 0 &&
          scrapFolderList.map((item) => (
            <ScrapFolderListItem
              key={item.folderIdx}
              title={item.folderName}
              folderIdx={item.folderIdx}
            />
          ))}
      </div>

      <LogoutButton />
    </aside>
  );
};

export default Sidebar;
