import SidebarItem from "./SidebarItem";
import { SIDEBAR_ITEM_SIZE } from "../../constants/sidebar-constants";
import { useRecoilValue } from "recoil";
import { userInfoState } from "../../store/user";
import CircleImageFrame from "../common/CircleImageFrame";

const UserProfileTab = () => {
  const userInfo = useRecoilValue(userInfoState);
  // console.log(userInfo);


  return (
    <SidebarItem
      className={`${SIDEBAR_ITEM_SIZE.lg} bg-transparent hover:bg-stone-200`}
    >
      <CircleImageFrame imageUrl={userInfo.imageUrl}/>
      <p className="text-base font-bold text-center mx-2">{userInfo.name}</p>
    </SidebarItem>
  );
};

export default UserProfileTab;
