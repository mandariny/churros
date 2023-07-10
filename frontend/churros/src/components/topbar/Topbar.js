import PageIndicator from "./PageIndicator"
import ArticleSearchButton from "./ArticleSearchButton";

const Topbar = ({searchOnClick}) => {
    return <header className="flex flex-row justify-between items-center h-14 m-1 p-2">
        <PageIndicator/>
        <ArticleSearchButton searchOnClick={searchOnClick} />
    </header>
}

export default Topbar;