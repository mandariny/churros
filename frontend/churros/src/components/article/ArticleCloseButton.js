import { IoClose } from "react-icons/io5";
import { api } from "../../axios-instance/api";

const ArticleCloseButton = ({ onClose, articleIdx }) => {
  const fetchData = async () => {
    try {
      const response = await api.post(`/news/dislike`, {articleId: articleIdx});
      const { result } = response.data;
      // console.log(`disliked ${articleIdx}: ${result}`);
    } catch (error) {
      console.log(error);
    }
  };

  const onClick = (e) => {
    e.stopPropagation();
    fetchData();
  };
  
  return (
    <div
      className="absolute block right-0 top-0 z-20 m-2 p-1 text-gray-200 rounded-lg hover:bg-red-500 transition duration-300 hover:text-white transition duration-300"
      onClick={onClick}
    >
      <IoClose size={30} />
    </div>
  );
};

export default ArticleCloseButton;
