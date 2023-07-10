import logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s|%(levelname)s|%(message)s')

try:
    import naver_news_crawling
    import naver_enter_news_crawling
except Exception as e:
    logging.error(f'ETC|{e}')
    # print(make_log('ERROR', 'ETC'), '[', e, ']')

def main():
    naver_news_crawling.main()
    naver_enter_news_crawling.main()

if __name__ == '__main__':
    try:
        main()
    except Exception as e:
        logging.error(f'ETC|{e}')
        # print(make_log('ERROR', 'ETC'), '[', e, ']')
        
