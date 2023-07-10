import os
import sys
sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))

from app.recommend_models.model_LDA import LDAmodel

green = LDAmodel('green')
blue = LDAmodel('blue')
models = [green, blue]

flag = 0
remodel = models[flag]

def remodel_recommend_model():
    global flag
    global remodel
    flag = (flag + 1) % 2
    models[flag].change_model_files()
    remodel = models[flag]

    return {"result" : "success"}


def test_remodel_recommend_model():
    before = remodel.name
    remodel_recommend_model()

    after = remodel.name
    remodel_recommend_model()

    after_after = remodel.name

    assert before == 'green'
    assert after == 'blue'
    assert after_after == 'green'
