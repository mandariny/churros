import { atom } from "recoil";
import { recoilPersist } from "recoil-persist";

const { persistAtom } = recoilPersist();

export const selectedSidebarItemIdState = atom({
  key: "selectedSidebarItemIdState",
  default: "",
  effects: [persistAtom],
});

export const showScrapFolderListState = atom({
  key: "showScrapFolderListState",
  default: false,
  effects: [persistAtom],
});

export const scrapFolderListState = atom({
  key: "scrapFolderList",
  default: [],
  effects: [persistAtom],
});
