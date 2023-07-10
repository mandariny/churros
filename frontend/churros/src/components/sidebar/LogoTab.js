import SidebarItem from "./SidebarItem";
import { SIDEBAR_ITEM_SIZE } from "../../constants/sidebar-constants";
import { useNavigate } from "react-router-dom";

const LogoTab = () => {
  const navigate = useNavigate();
  const homeOnClick = () => {
    navigate("/");
  };
  return (
    <SidebarItem className={`${SIDEBAR_ITEM_SIZE.lg} bg-stone-200`}>
      <div onClick={homeOnClick} className="inline-block flex cursor-pointer">
        <img
          src="/favicon.ico"
          alt="logo"
          className="object-scale-down h-12 w-12 m-3"
        />
        <p className="align-text-center font-cooper text-2xl flex items-center">
          CHURROS
        </p>
      </div>
    </SidebarItem>
  );
};

export default LogoTab;
