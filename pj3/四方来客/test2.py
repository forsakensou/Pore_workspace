 # -*- coding: utf-8 -*-
import numpy as np
import copy
from operator import itemgetter
 
goal = {}
 
def get_location(vec, num):    #根据num元素获取num在矩阵中的位置
    row_num = vec.shape[0]     #numpy-shape函数获得矩阵的维数
    line_num = vec.shape[1]
    for i in range(row_num):
       for j in range(line_num):
            if num == vec[i][j]:
                 return i, j
 
def get_actions(vec):    #获取当前位置可以移动的下一个位置，返回移动列表
    row_num = vec.shape[0]
    line_num = vec.shape[1]
    
    (x, y) = get_location(vec, 0)    #获取0元素的位置
    action = [(0, 1), (0, -1), (1, 0), (-1, 0)]
    
    if x == 0:    #如果0在边缘则依据位置情况，减少0的可移动位置
        action.remove((-1, 0))
    if y == 0:
        action.remove((0, -1))
    if x == row_num - 1:
        action.remove((1, 0))
    if y == line_num - 1:
        action.remove((0, 1))
        
    return list(action)

def result(vec, action):    #移动元素，进行矩阵转化
    (x, y) = get_location(vec, 0)    #获取0元素的位置
    (a, b) = action    #获取可移动位置
                                
    n = vec[x+a][y+b]    #位置移动，交换元素
    s = copy.deepcopy(vec)
    s[x+a][y+b] = 0
    s[x][y] = n
    
    return s
    
def get_ManhattanDis(vec1, vec2):    #计算两个矩阵的曼哈顿距离,vec1为目标矩阵,vec2为当前矩阵
    row_num = vec1.shape[0]
    line_num = vec1.shape[1]
    dis  = 0
   
    for i in range(row_num):
        for j in range(line_num):
            if vec1[i][j] != vec2[i][j] and vec2[i][j] != 0:
                k, m = get_location(vec1, vec2[i][j])
                d = abs(i - k) + abs(j - m)
                dis += d  

    return dis

def expand(p, actions, step):                          #actions为当前矩阵的可扩展状态列表,p为当前矩阵,step为已走的步数
    children = []                                      #children用来保存当前状态的扩展节点
    for action in actions:
        child = {}
        child['parent'] = p
        child['vec'] = (result(p['vec'], action))
        child['dis'] = get_ManhattanDis(goal['vec'], child['vec'])
        child['step'] = step + 1                       #每扩展一次当前已走距离加1
        child['dis'] = child['dis'] + child['step']    #更新该节点的f值  f=g+h（step+child[dis]）                     
        child['action'] = get_actions(child['vec'])
        children.append(child)
    
    return children

def node_sort(nodelist):    #按照节点中字典的距离字段对列表进行排序,从大到小
    return sorted(nodelist, key = itemgetter('dis'), reverse=True)

def get_input(num):
    A = []
    for i in range(num):
        temp = []
        p = []
        s = input()
        temp = s.split(' ')
        for t in temp:
            t = int(t)
            p.append(t)
        A.append(p)
  
    return A  

def get_parent(node):
    q = {}
    q = node['parent']   
    return q
        
def test():
    openlist = []    #open表
    close = []       #存储扩展的父节点
    
    print('请输入矩阵的行数')
    num = int(input())  
    
    print("请输入初始矩阵A")
    A = get_input(num)
 
    print("请输入目标矩阵B")
    B = get_input(num)
    
    print("请输入结果文件名")
    resultfile = input()
    
    goal['vec'] = np.array(B)   #建立矩阵
   
    p = {}
    p['vec'] = np.array(A)
    p['dis'] = get_ManhattanDis(goal['vec'], p['vec'])
    p['step'] = 0
    p['action'] = get_actions(p['vec'])
    p['parent'] = {}

    if (p['vec'] == goal['vec']).all():
        return
    
    openlist.append(p)
    
    while openlist:
        
        children = []
       
        node = openlist.pop()    #node为字典类型，pop出open表的最后一个元素
        close.append(node)  #将该元素放入close表
      
        if (node['vec'] == goal['vec']).all():    #比较当前矩阵和目标矩阵是否相同
         
            h = open(resultfile,'w',encoding='utf-8',)  #将结果写入文件  并在控制台输出
            h.write('搜索树规模：' + str(len(openlist)+len(close)) + '\n')
            h.write('close：' + str(len(close)) + '\n')
            h.write('openlist：' + str(len(openlist)) + '\n')
            h.write('路径长：' + str(node['dis']) + '\n')
            
            h.write('解的路径：' + '\n')
            i = 0
            way = []
            while close:
                way.append(node['vec'])  #从最终状态开始依次向上回溯将其父节点存入way列表中
                node = get_parent(node)
                if(node['vec'] == p['vec']).all():
                    way.append(node['vec'])
                    break
            while way:
                i += 1
                h.write(str(i) + '\n')
                h.write(str(way.pop()) + '\n')
            h.close()
            f = open(resultfile,'r',encoding='utf-8',)
            print(f.read())
            
            return
        
        children = expand(node, node['action'], node['step'])    #如果不是目标矩阵，对当前节点进行扩展，取矩阵的可能转移情况
        
        for child in children:     #如果转移之后的节点，既不在close表也不再open表则插入open表，如果在close表中则舍弃，如果在open表则比较这两个矩阵的f值，留小的在open表
            f = False
            flag = False
            j = 0
            for i in range(len(openlist)):
                if (child['vec'] == openlist[i]['vec']).all():
                    j = i
                    flag = True
                    break
            for i in range(len(close)):
                if(child['vec'] == close[i]).all():
                    f = True
                    break
            if  f == False and flag == False :
                openlist.append(child)
                
            elif flag == True:
                if child['dis'] < openlist[j]['dis']:
                    del openlist[j]
                    openlist.append(child)
                   
        
        openlist = node_sort(openlist)   #对open表进行从大到小排序
    
test()


