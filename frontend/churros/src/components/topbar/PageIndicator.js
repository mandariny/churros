import { useEffect,useState } from 'react';
import { useLocation } from 'react-router-dom';
import {FaHeart, FaBookmark, FaSearch, FaNewspaper } from "react-icons/fa";
import {BiNews} from "react-icons/bi"
const PageIndicator = () => {
  const location = useLocation();
  const [pageTitle, setPageTitle] = useState("1면 기사");
  const [pageIcon, setPageIcon] = useState(<FaNewspaper />);
  useEffect(() => {
    const headers = {"":"1면 기사", "likes":"좋아요한 기사","scraps":"스크랩 한 기사 ","search":"기사 검색"}
    // Get the current path name from the location object
    const pathName = location.pathname;
    setPageTitle(headers[pathName.split("/")[1]])
    // Set the page title based on the current path name
    switch (pathName.split("/")[1]) {
      case 'likes':
        setPageIcon(<FaHeart size={20} style={{ color: "red" }}/>);
        break;
      case 'scraps':
        setPageIcon(<FaBookmark size={20} style={{ color: "FFB240" }}/>);
        break;
      case 'search':
        setPageIcon(<FaSearch  size={20} />);
        break;
      default:
        setPageIcon(<BiNews  size={20} />);
        break;
    }
  }, [location]);
  return (
    <div className="flex flex-row justify-start items-center mx-1 p-1">
      <div className="mx-2">{pageIcon}</div>
      <h1 className="text-xl font-bold">{pageTitle}</h1>
    </div>
  );
};

export default PageIndicator;
