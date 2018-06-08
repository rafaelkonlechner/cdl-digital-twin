import math

def to_rad(arg):
    return (float(arg) / 1900.0) * math.pi

def from_rad(arg):
    return int((float(arg) / math.pi) * 1900.0)

def rot_direction(position, target):
    if position < target:
        return "pos"
    else:
        return "neg"

def break_distance(speed, deceleration):
    x = speed
    dist = 0
    while (x > 0):
        dist += x
        x -= deceleration
    return dist

def calc_speed(position, target, speed, max_speed, acceleration, deceleration):
    if not math.isclose(position, target, rel_tol = acceleration, abs_tol = acceleration):
        distance = abs(target - position)
        direction = rot_direction(position, target)
        if distance > break_distance(abs(speed) + acceleration, deceleration):
            if direction == "pos":
                new_speed = min(speed + acceleration, max_speed)
            if direction == "neg":
                new_speed = max(speed - acceleration, -max_speed)
        else:
            if speed > 0:
                new_speed = max(0, speed - deceleration)
            if speed < 0:
                new_speed = min(0, speed + deceleration)
    else:
        new_speed = 0
    return new_speed
