import os
import time
import sys

def CROSS(colnum):
    tl = "+"
    for i in range(int(colnum)-2):
        tl += "-"
    tl += "+"
    print tl

def EMPTY(rownum, colnum):
    empt = (int(rows)-26) - 1
    tl = ""
    for i in range(int(colnum)-2):
        tl += " "
    for i in range(empt):
        print tl

def TITLE(TITLE, colnum, lf):
    empt = (int(colnum) - len(TITLE) - 2)/2
    tr = "+"
    for i in range(empt):
        tr += " "
    tr += TITLE
    for i in range(empt):
        tr += " "
    if lf:
        tr += " +"
    else:
        tr += "+"
    print tr

def quad1(colnum, fwh, pdh, ah, hs):
    tl = "+"
    for i in range(int(colnum)-2):
        tl += " "
    tl += "+"
    print tl

    l1 = len("fireweed hits") + len("pandora hits")
    l2 = len("arugula hits") + len("honey suckle hits")

    empt1 = (int(colnum)-7-l1)/2
    tr="+"
    for i in range(empt1):
        tr+= " "
    tr += "FIREWEED HITS:" + str(fwh)
    tr += "  "
    tr += "PANDORA HITS:" + str(pdh)
    for i in range(empt1):
        tr+= " "
    tr += "+"
    print tr

    tl = "+"
    for i in range(int(colnum)-2):
        tl += " "
    tl += "+"
    print tl

    empt2 = (int(colnum)-7-l2)/2
    tr2="+"
    for i in range(empt2-1):
        tr2+= " "
    tr2 += "ARUGULA HITS:" + str(ah)
    tr2 += "  "
    tr2 += "HONEYSUCKLE HITS:" + str(hs)
    for i in range(empt1):
        tr2 += " "
    tr2 += "+"
    print tr2

def quad2(colnum, mp1, mp2, fr1, fr2):
    tl = "+"
    for i in range(int(colnum)-2):
        tl += " "
    tl += "+"
    print tl

    l1 = len("MISFIRE PENALTY (NO PLANTS)")
    l2 = len("MISFIRE PENALTY (WRONG PLANTS)")
    l3 = len("FINISH RING BONUS (POLLINATE)")
    l4 = len("FINISH RING BONUS (DONATION)")

    empt1 = (int(colnum)-4-l1)/2
    tr="+"
    for i in range(empt1):
        tr+= " "
    tr += "MISFIRE PENALTY (NO PLANTS):" + str(mp1)
    for i in range(empt1):
        tr+= " "
    tr += "+"
    print tr

    empt2 = (int(colnum)-4-l2)/2
    tr="+"
    for i in range(empt2):
        tr+= " "
    tr += "MISFIRE PENALTY (WRONG PLANTS):" + str(mp2)
    for i in range(empt2+1):
        tr+= " "
    tr += "+"
    print tr

    empt3 = (int(colnum)-4-l3)/2
    tr="+"
    for i in range(empt3):
        tr+= " "
    tr += "FINISH RING BONUS (POLLINATE):" + str(fr1)
    for i in range(empt3):
        tr+= " "
    tr += "+"
    print tr

    empt4 = (int(colnum)-4-l1)/2
    tr="+"
    for i in range(empt4):
        tr+= " "
    tr += "FINISH RING BONUS (DONATION):" + str(fr2)
    for i in range(empt4-1):
        tr+= " "
    tr += "+"
    print tr


def SCORE(SCORE, colnum, lf):
    empt = (int(colnum) - len(str(SCORE)) - len("TOTAL SCORE:") - 2)/2
    tr = "+"
    for i in range(empt):
        tr += " "
    tr += "TOTAL SCORE:" + str(SCORE)
    for i in range(empt):
        tr += " "
    if lf:
        tr += " +"
    else:
        tr += "+"
    print tr

def POL_RATIO(NUM, DEN, colnum, lf):
    empt = (int(colnum) - len("POLLINATION HIT RATIO:") - 5)/2
    tr = "+"
    for i in range(empt):
        tr += " "
    tr += "POLLINATION HIT RATIO:" + str(NUM) + "/" + str(DEN)
    for i in range(empt):
        tr += " "
    if lf:
        tr += " +"
    else:
        tr += "+"
    print tr

if __name__ == "__main__":
    ascititle = """"""
    while True:
        rows, columns = os.popen('stty size', 'r').read().split()
        f = open("/home/vkumar9/out.txt")
        gamepoints = []
        for ind, item in enumerate(f.readlines()):
            if ind != 0:
                gamepoints.append(int(item))
            else:
                gamepoints.append(item.rstrip())
        os.system("clear")
        CROSS(columns)
        print ascititle
        #EMPTY(rows, columns)
        CROSS(columns)
        TITLE(gamepoints[0], columns, True)
        CROSS(columns)
        TITLE("COLLECTIONS", columns, False)
        quad1(columns, gamepoints[1], gamepoints[2], gamepoints[3], gamepoints[4])
        CROSS(columns)
        TITLE("DONATIONS", columns, False)
        quad1(columns, gamepoints[5], gamepoints[6], gamepoints[7], gamepoints[8])
        CROSS(columns)
        TITLE("BONUSES/PENALTIES", columns, False)
        quad2(columns, gamepoints[9], gamepoints[10], gamepoints[11], gamepoints[12])
        CROSS(columns)
        POL_RATIO(gamepoints[13], gamepoints[14], columns, False)
        CROSS(columns)
        SCORE(gamepoints[15], columns, False)
        CROSS(columns)
        #EMPTY(rows, columns)
        CROSS(columns)
        time.sleep(1)
        os.system("clear")
