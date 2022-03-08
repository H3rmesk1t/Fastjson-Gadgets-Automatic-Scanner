#!/usr/bin/env python
import os.path
import argparse
import subprocess
from javalang.tree import *
from javalang.parse import parse

def logo():
    """
    Generate the logo and print it on terminal.
    :return:
    """
    logo_file = open(r'logo.txt')
    logo_file = logo_file.read()
    print('\033[1;36m\t' + logo_file + '\033[0m')


def command():
    """
    Use the command to obtain the jar packages in the terminal.
    :return:
    """
    parser = argparse.ArgumentParser(prog='python main.py')
    parser.add_argument('jar', help='Enter the jar to be scanned', type=str)
    parser.add_argument('operating_system', help='Enter the operating system Windows or Linux or MacOS', type=str)
    args = parser.parse_args()
    return args.jar, args.operating_system


def blacklist_generate():
    """
    Generate blacklist.
    :return:
    """
    blacklist_file = open(r'./blacklist.txt', 'r')
    blacklist = blacklist_file.read().splitlines()
    blacklist_file.close()
    return blacklist


def jar_process(jar, operating_system):
    """
    Generate the jar and create appropriate folders.
    :param jar:
    :param operating_system:
    :return:
    """
    # Process the jar package path according to the operating system.
    if operating_system == 'Windows':
        jar_name = jar.split('\\')[-1].split('.')[0]
    elif operating_system == 'Linux' or operating_system == 'MacOS':
        jar_name = jar.split('/')[-1].split('.')[0]

    jar_dir = './project/' + jar_name
    jar_file_dir = './project/' + jar_name + '/file'
    subprocess.run(['mkdir', jar_dir])
    subprocess.run(['mkdir', jar_file_dir])
    return jar_dir, jar_file_dir, jar_name


def jar_decompile(jar, jar_dir, jar_file_dir, jar_name):
    """
    Decompile jar package.
    :param jar:
    :param jar_dir:
    :param jar_file_dir:
    :param jar_name:
    :return:
    """
    # Decompile operation.
    subprocess.run(['java', '-cp', './java-decompiler.jar', 'org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler', jar, jar_dir])
    # Unzip operation.
    ret = subprocess.run(['unzip', '-d', jar_file_dir, jar_dir + '/' + jar_name + '.jar'])
    if ret.returncode == 0:
        print('\033[1;31m[+] jar package decompile successfully.\033[0m')


def javafile_generate(content):
    """
    Generate java file list from jar package that had been decompiled.
    :param content:
    :return:
    """
    if os.path.exists(content) is False:
        return []

    content_path = os.path.abspath(content)
    files = os.listdir(content_path)
    java_file = []
    for file in files:
        _file = os.path.join(content_path, file)
        if os.path.isfile(_file):
            if file.endswith('.java'):
                java_file.append(_file)
        else:
            java_file.extend(javafile_generate(_file))
    return java_file


def lookup_detect(method_declaration):
    """
    Final check for each method declaration.
    :param method_declaration:
    :return:
    """
    available_variables = []
    for path, node in method_declaration:
        # Determine whether to call the lookup method.
        if isinstance(node, MethodInvocation) and node.member == 'lookup':
            # Determine if there is only one parameter.
            if len(node.arguments) != 1:
                continue
            # Determine whether a parameter is a controllable variable.
            argument = node.arguments[0]
            if isinstance(argument, Cast):
                available_variables.append(argument.expression.member)
            if isinstance(argument, MemberReference):
                available_variables.append(argument.member)
            if isinstance(argument, This):
                return True
    if len(available_variables) == 0:
        return False

    # Determine whether the parameters of the lookup come from the method, because only the input parameters are considered controllable.
    for parameter in method_declaration.parameters:
        parameter_name = parameter.name
        if parameter_name in available_variables:
            return True
    return False


def class_declaration_generate(root_tree):
    """
    Filter eligible classes.
    :param root_tree:
    :return:
    """
    class_list = []
    # Cannot implement DataSource and RowSet interfaces.
    black_interface = ('DataSource', 'RowSet')
    for node in root_tree.types:
        # Class declarations are not parsed.
        if isinstance(node, ClassDeclaration) is False:
            continue

        # Cannot inherit Classloader class.
        if node.extends is not None and node.extends.name == 'ClassLoader':
            continue

        # Determine whether the interface is in the blacklist.
        interface_flag = False
        if node.implements is None:
            node.implements = []
        for implement in node.implements:
            if implement.name in black_interface:
                interface_flag = True
                break
        if interface_flag is True:
            continue

        # Check if there is a constructor with no parameters.
        constructor_flag = False
        for constructor_declaration in node.constructors:
            if len(constructor_declaration.parameters) != 0:
                constructor_flag = True
                break
        if constructor_flag is False:
            continue

        class_list.append(node)
    return class_list


def scanner_dir(jar_name, jar_file_dir):
    """
    Traverse the decompiled directory to find the Java file.
    :param jar_name:
    :param jar_file_dir:
    :return:
    """
    global java_file_list
    java_file_list = []
    contents = os.listdir(jar_file_dir)
    for content in contents:
        file = os.path.join(jar_file_dir, content)
        if os.path.isfile(file):
            continue
        java_file_list.extend(javafile_generate(file))
    print('\033[1;31m[+] ' + jar_name + ' jar obtains a total of ' + str(len(java_file_list)) + ' Java files.\033[0m')
    for java_file in java_file_list:
        scanner_file(jar_name, java_file)


def scanner_file(jar_name, java_file, blacklists=None):
    """
    Scan and identify Java file.
    :param java_file:
    :return:
    """
    # Get java file content.
    file = open(java_file, 'r')
    file_content = file.read()
    file.close()

    # Get classes already known to be in the blacklist.
    if blacklists is None:
        blacklists = blacklist_generate()
    blacklists_length = len(blacklists)

    # Quickly judge the string and directly exclude files without the InitialContext keyword.
    if 'InitialContext(' not in file_content:
        return False
    try:
        # Use javalang library to parse source code to get Abstract Syntax Tree.
        root_tree = parse(file_content)
    except:
        return False

    class_declaration_list = class_declaration_generate(root_tree)
    # Iterate over class declarations
    for class_declaration in class_declaration_list:
        # Iterate over method declarations.
        for method_declaration in class_declaration.methods:
            if lookup_detect(method_declaration) is True:
                # Determine whether the scanned gadgets are in the blacklist.
                index = 0
                for blacklist in blacklists:
                    if blacklist not in java_file:
                        index += 1
                # Output eligible gadgets.
                if index == blacklists_length:
                    result = f'{java_file} {method_declaration.name}'
                    print('\033[1;31m[+] It is found that the gadget may be exploited and the file name and the method name are as follows.\033[0m')
                    print('\033[1;36m[+] ' + result + '\033[0m')
                    save_results('./scan_results/' + jar_name + '.txt', java_file, method_declaration.name)


def save_results(file_path, java_file, method_declaration_name):
    """
    Save scan results.
    :param file_path:
    :param java_file:
    :param method_declaration_name:
    :return:
    """
    file_name = open(file_path, 'a')
    file_name.write('[+] java_file: ' + java_file + '\n')
    file_name.write('[+] method_declaration_name: ' + method_declaration_name + '\n\n')
    file_name.close()


def main():
    """
    Main function.
    :return:
    """
    jar, operating_system = command()
    jar_dir, jar_file_dir, jar_name = jar_process(jar, operating_system)
    jar_decompile(jar, jar_dir, jar_file_dir, jar_name)
    scanner_dir(jar_name, jar_file_dir)
    print('\033[1;31m[+] Scan result saved successfully.\033[0m')


if __name__ == '__main__':
    logo()
    main()
