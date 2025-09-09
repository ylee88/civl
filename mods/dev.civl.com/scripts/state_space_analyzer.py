import sys
import os

import inspect
import dataclasses

import state_space
from state_space import *
from state_space_parser import parseStateSpace as parse

def _getShortDoc(doc, prepend=""):
    lines = doc.split('\n')
    newLines = []
    for i, line in enumerate(lines):
        if line.strip() == '':
            break
        
        newLines.append(prepend+line)

    return '\n'.join(newLines)

def _validObj(name, obj):
    return name[0] != '_' and (inspect.isfunction(obj) or inspect.isclass(obj))

def _brieflyDescribe(name, obj):
    print(" *  "+name, end="")
    if inspect.isfunction(obj):
        print(inspect.signature(obj))
    else:
        print()

    if (doc := inspect.getdoc(obj)):
        print(_getShortDoc(doc, "      "))

def _describe(name, obj):
    if name != __name__:
        print(name)
        print('='*len(name))
        if (doc := inspect.getdoc(obj)):
            print(doc)
    print()

    if dataclasses.is_dataclass(obj):
        print("Fields")
        print("------")
        for field in dataclasses.fields(obj):
            print(f"{field.name}: {field.type.__name__}")
        print()

    functions = []
    classes = []

    for member in inspect.getmembers(obj):
        if member[0][0] == '_':
            continue

        if inspect.isfunction(member[1]):
            functions.append(member)
        elif inspect.isclass(member[1]):
            classes.append(member)

    print("Functions")
    print("---------")
    for f in functions:
        _brieflyDescribe(f[0], f[1])
    print()

    print("Classes")
    print("-------")
    for c in classes:
        _brieflyDescribe(c[0], c[1])
    print()

def _getObjFromSpecifier(specifier):
    currentObj = (__name__, sys.modules[__name__])
    currentSubSpecifier = ""
    
    for objName in specifier.split('.'):
        if objName == "":
            continue

        foundNextObj = False
        for member in inspect.getmembers(currentObj[1]):
            if not _validObj(member[0], member[1]):
                continue
            
            if objName == member[0]:
                currentObj = member
                foundNextObj = True
                break
        
        if not foundNextObj:
            notFoundString = "Failed to find \""+objName+"\""
            if currentSubSpecifier != "":
                notFoundString += " under object \""+currentSubSpecifier[1:]+"\""
            print(notFoundString)
            return None

        currentSubSpecifier += "."+objName

    return currentObj

def help(ident=""):
    """
    Prints out description of current module, or of the object specified by 'ident' if it is nonempty.

    If 'ident' is empty, then prints out all available functions and classes of this interactive session.
    If 'ident' is nonempty, then it prints a description of the function or class specified by 'ident', along with all available functions and classes available within this object.
    'ident' can access subobjects using '.' as a separator.
    """
    if (obj := _getObjFromSpecifier(ident)):
        _describe(obj[0], obj[1])

##############################################

print("CIVL interactive state space analyzer")
print("=====================================")
print("Execute function `help()` to get list of methods and structures available to you.\n")

if len(sys.argv) > 1:
    if not os.path.isfile(sys.argv[1]):
        print("File does not exist: "+sys.argv[1])
        exit()

    print("Parsing file: "+sys.argv[1])
    space = parse(sys.argv[1])
    print("Parse complete. Resulting state space contained in variable 'space'.")

