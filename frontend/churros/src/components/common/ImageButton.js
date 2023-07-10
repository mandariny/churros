const ImageButton = ({onClick, src, alt, className}) => {
    return <button onClick={onClick} className={`inline-flex items-center ${className}`}>
        <img src={src} alt={alt} />
    </button>
}

export default ImageButton;