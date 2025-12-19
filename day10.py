from z3 import Int, Optimize, Sum, sat
import re

def solve_machine(buttons, target):
    # buttons: list[list[int]]  e.g. [[3], [1,3], [2], [2,3], [0,2], [0,1]]
    # target:  list[int]        e.g. [3,5,4,7]
    n_counters = len(target)
    m = len(buttons)

    x = [Int(f"x{j}") for j in range(m)]
    opt = Optimize()

    # x_j >= 0
    for j in range(m):
        opt.add(x[j] >= 0)

    # For each counter k: sum_j A[k][j]*x_j == target[k]
    for k in range(n_counters):
        expr = Sum([x[j] for j in range(m) if k in buttons[j]])
        opt.add(expr == target[k])

    opt.minimize(Sum(x))

    if opt.check() != sat:
        return None 

    model = opt.model()
    presses = [model.eval(xj).as_long() for xj in x]
    return sum(presses), presses


file_path = 'app/src/main/resources/day10.txt'
with open(file_path, 'r') as file:
    lines_list = file.readlines()

result = 0
for line in lines_list:
    buttons = [
        list(map(int, grp.split(",")))
        for grp in re.findall(r"\(([^)]*)\)", line)
    ]
    target = list(map(int, re.search(r"\{([^}]*)\}", line).group(1).split(",")))
    
    best, presses = solve_machine(buttons, target)
    print(best, presses)
    result += best

print(result)
