from flask import Flask, request
import requests

app = Flask(__name__)


@app.before_request
def before():
    """
    针对app实例定义全局拦截器
    """
    url = request.path
    if "/image" in url:
        artworkUrl = "https://i.pximg.net" + url.split("/image")[1]

        return (
            requests.get(artworkUrl, headers={"Referer": "https://www.pixiv.net", "Host": "www.pixiv.net"}).content,
            200,
            {
                "Content-Type": "image/jpeg; image/png; image/gif"
            }
        )


app.run()