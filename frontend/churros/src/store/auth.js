import { atom } from "recoil";
import { recoilPersist } from "recoil-persist";

const { persistAtom } = recoilPersist();

export const accessTokenState = atom({
  key: "accessToken",
  default: null,
  effects: [persistAtom],
});

export const refreshTokenState = atom({
  key: "refreshToken",
  default: null,
  effects: [persistAtom],
});
