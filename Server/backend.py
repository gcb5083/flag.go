from flask import Flask

app = Flask(__name__)

@app.route('/gps', methods=['POST'])
    content = request.json          #TODO
    score = update_score(content[0], conent[1], content[2])
    return jsonify({"score":score})

@app.route('/hints', methods=['GET'])
    hints = get_hints()
    return jsonify({"score":hints})
