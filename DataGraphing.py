import pandas as pd
import matplotlib.pyplot as plt

from typing import List

def readCSV(filename : str, features : List[str])->object:
    df = pd.read_csv(f"{filename}.csv)
    df = df[features]
    return df

getFeatures = lambda inputStr : inputStr.split(" ")

#def analyzeStatistics(dataFrame : object):
#    print(dataFrame.describe())


if __name__ == "__main__":
    fileName = input("Enter path of CSV ")
    features = getFeatures(input("Enter statistics to plot "))

    try:
        df = readCSV(fileName, features)
    except KeyError:
        print(f"invalid statistic identifier in features: {features}")

    df.plot()
    plt.show()

#    analyzeStatistics(df)
