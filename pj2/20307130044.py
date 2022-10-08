# -*- coding: utf-8 -*-  
"""
Deobscure class name(use debug directives as source name) for PNF Software's JEB2.
"""
__author__ = 'Ericli'

from com.pnfsoftware.jeb.client.api import IScript
from com.pnfsoftware.jeb.core import RuntimeProjectUtil
from com.pnfsoftware.jeb.core.units.code import ICodeUnit, ICodeItem
from com.pnfsoftware.jeb.core.units.code.android import IDexUnit
from com.pnfsoftware.jeb.core.actions import Actions, ActionContext, ActionCommentData, ActionRenameData
from java.lang import Runnable


class JEB2DeGuardClass(IScript):
    def run(self, ctx):
        ctx.executeAsync("Running deguard ...", JEB2AutoRename(ctx))
        print('Done')


class JEB2AutoRename(Runnable):
    def __init__(self, ctx):
        self.ctx = ctx

    def run(self):
        ctx = self.ctx
        engctx = ctx.getEnginesContext()
        if not engctx:
            print('Back-end engines not initialized')
            return

        projects = engctx.getProjects()
        if not projects:
            print('There is no opened project')
            return

        prj = projects[0]

        units = RuntimeProjectUtil.findUnitsByType(prj, IDexUnit, False)
        
        f = open('C:\Users\Administrator\Desktop\JEB_Decompiler_3.19.1_Professional\jeb-pro-3.19.1.202005071620\scripts\map.txt','r')
        origin = []
        change = []
        change_field_class = {}
        origin_field = []
        change_field = []
        while True:
            string = f.readline()
            if string.__len__() == 0:
                break
            origin_name , change_name = string.split(" -> ")
            change_name = change_name.strip('\n')
            if '    ' in origin_name:
                origin_name = origin_name.split(' ')[-1]
                if origin[-1] in change_field_class.keys():
                    change_field_class[origin[-1]].append([origin_name,change_name])
                else:
                    change_field_class[origin[-1]] = []
                    change_field_class[origin[-1]].append([origin_name,change_name])
            else:
                origin_name = origin_name.replace('.', '/')
                origin_name = 'L' + origin_name + ';'
                change_name = change_name.replace('.', '/')
                change_name = 'L' + change_name + ';'
                origin.append(origin_name)
                change.append(change_name)
        print(origin)
        print(change)
        f.close()

        for unit in units:
            classes = unit.getClasses()
            if classes:
                for clazz in classes:
                    # print(clazz.getName(True), clazz)
                    sourceIndex = clazz.getSourceStringIndex()
                    clazzAddress = clazz.getAddress()
                    # clazz_field = clazz.getFields()
                    # if (clazz_field.__len__() != 0):
                       # print(clazz_field[0].getName())
                        # self.rename_class(unit, clazz_field[0], "iloveyou", True)
                    if sourceIndex == -1 or '$' in clazzAddress:# Do not rename inner class
                        # print('without have source field', clazz.getName(True))
                        continue

                    # print(clazz.getName(True), sourceIndex, sourceStr, clazz)
                    for i in range(0,origin.__len__()):
                        if origin[i] == clazzAddress:
                            if origin[i] in change_field_class.keys():
                                clazz_field = clazz.getFields()
                                for field in clazz_field:
                                    for field_tuple in change_field_class[origin[i]]:
                                        if field.getName() == field_tuple[0]:
                                            self.comment(unit, field, field.getName(True))
                                            self.rename(unit, field, field_tuple[1], True)
                                clazz_method = clazz.getMethods()            
                                for method in clazz_method:
                                    for method_tuple in change_field_class[origin[i]]:
                                        if method.getName() == (method_tuple[0]).split('(')[0]:
                                            self.comment(unit, method, method.getName(True))
                                            self.rename(unit, method, method_tuple[1], True)
                            if origin[i] == change[i]:
                                print('Same name: %s' % origin[i])
                                continue
                            self.comment(unit, clazz, clazz.getName(True))  # Backup origin clazz name to comment
                            sourceStr = (change[i].split('/'))[-1]
                            sourceStr = sourceStr.replace(';','')
                            self.rename(unit, clazz, sourceStr, True)  # Rename to source name

    def rename(self, unit, originClazz, sourceName, isBackup):
        actCtx = ActionContext(unit, Actions.RENAME, originClazz.getItemId(), originClazz.getAddress())
        actData = ActionRenameData()
        actData.setNewName(sourceName)

        if unit.prepareExecution(actCtx, actData):
            try:
                result = unit.executeAction(actCtx, actData)
                if result:
                    print('rename to %s success!' % sourceName)
                else:
                    print('rename to %s failed!' % sourceName)
            except Exception, e:
                print (Exception, e)

    def comment(self, unit, originClazz, commentStr):
        actCtx = ActionContext(unit, Actions.COMMENT, originClazz.getItemId(), originClazz.getAddress())
        actData = ActionCommentData()
        actData.setNewComment(commentStr)

        if unit.prepareExecution(actCtx, actData):
            try:
                result = unit.executeAction(actCtx, actData)
                if result:
                    print('comment to %s success!' % commentStr)
                else:
                    print('comment to %s failed!' % commentStr)
            except Exception, e:
                print (Exception, e)
