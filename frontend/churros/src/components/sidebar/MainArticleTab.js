import { SIDEBAR_ITEM_SIZE } from "../../constants/sidebar-constants";
import { IoBookOutline } from "react-icons/io5";
import SidebarNavLink from "./SidebarNavLink";

const MainArticleTab = () => {
  const navigateTo = "/"

  return (
    <SidebarNavLink
      navigateTo={navigateTo}
      className={`${SIDEBAR_ITEM_SIZE.sm} hover:bg-stone-300`}
    >
      <div className="block w-7" />
      <IoBookOutline className="p-2" size={35} style={{ color: "837F79" }} />
      <p className="text-sm text-center">추천 기사</p>
    </SidebarNavLink>
  );
};

export default MainArticleTab;
