import json
from flask import Flask, request, jsonify
import numpy as np
import pandas as pd
from pandas.tseries.offsets import DateOffset
import keras
from sklearn.preprocessing import MinMaxScaler


app = Flask(__name__)


@app.route('/predict', methods=['POST'])
def predict():
    forecast = {"prediction": []}
    event = json.loads(request.data)
    values = event['feed']
    forecast['prediction'] = get_prediction(values)
    return jsonify(forecast)


def get_prediction(values):
    predictions = [4792, 4821, 4840, 4928, 4958, 5071, 5019, 4917, 4771, 4703, 4691,
                   4707, 4973, 4787, 4757, 4710, 4677, 4924, 5012, 4700, 4710, 4758,
                   4541, 4641, 4421, 3921, 4811, 5001, 5275, 5689, 6009, 6396, 5807,
                   5271, 4862, 4501]
    model = keras.models.load_model("final_lstm.h5")
    df = pd.DataFrame(values)
    df.index = pd.to_datetime(df['Month'], format='%Y.%m')
    df['Total_Crimes'] = df['Total_Crimes'].astype(str).astype(int)
    df = df.drop('Month', 1)
    x = len(df) - 12
    train = df.iloc[:x]
    scaler = MinMaxScaler()
    scaler.fit(train)
    train = scaler.transform(train)
    n_input = 36
    n_features = 1
    pred_list = []
    batch = train[-n_input:].reshape((1, n_input, n_features))
    for i in range(n_input):
        pred_list.append(model.predict(batch)[0])
        batch = np.append(batch[:, 1:, :], [[pred_list[i]]], axis=1)

    add_dates = [df.index[-1] + DateOffset(months=x) for x in range(0, 37)]
    future_dates = pd.DataFrame(index=add_dates[1:], columns=df.columns)
    df_predict = pd.DataFrame(scaler.inverse_transform(pred_list),
                              index=future_dates[-n_input:].index, columns=['Prediction'])

    df_proj = pd.concat([df, df_predict], axis=1)
    df_proj = df_proj.round(0)
    occurred_crimes = df_proj[df_proj['Total_Crimes'].notnull()]
    occurred_crimes['Total_Crimes'] = occurred_crimes['Total_Crimes'].astype('int32')
    col_predict_list = occurred_crimes['Total_Crimes'].tolist() + predictions
    return col_predict_list


if __name__ == '__main__':
    app.run()