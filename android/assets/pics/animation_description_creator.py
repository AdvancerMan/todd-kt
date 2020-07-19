import os
import os.path
import json
import PIL.Image

animation_types = [
    "STAY", "RUN", "JUMP", "FALL", "LANDING", "SHOOT"
]

loop_animation_types = {
    "STAY", "RUN"
}

animation_file_names = [name.lower() + ".png" for name in animation_types]

def add_to_anims(anims, path, anim_type):
    size = PIL.Image.open(path).size
    anims[anim_type] = {
        "path": path,
        "frameDuration": 100,
        "mode": "NORMAL" if anim_type not in loop_animation_types else "LOOP",
        "xywh": [0, 0, size[0], size[1]],
        "r": 1,
        "c": 1
    }

def normalize_rc(anims):
    w = 100000
    h = 100000
    for anim in anims.values():
        w = min(w, anim["xywh"][2])
        h = min(h, anim["xywh"][3])

    for anim in anims.values():
        anim["c"] = anim["xywh"][2] // w
        anim["r"] = anim["xywh"][3] // h

def write_anims(anims, root_path, name):
    anims_json = json.dumps(
        {
            "type": "anims",
            "name": name,
            "anims": anims
        },
        indent=2
    )
    path = os.path.join(root_path, name + ".json")
    print("Writing .json to", path)
    with open(path, 'w') as f:
        f.write(anims_json)

def create_description(root_path: str):
    anims = dict()
    for file_name in os.listdir(root_path):
        print()
        path = os.path.join(root_path, file_name)
        print("Scanning", path)
        if os.path.isdir(path):
            print(path, "is a directory, skipping...")
            continue
        if file_name not in animation_file_names:
            print(file_name, "is not a proper name, skipping... (proper names:", animation_file_names, ")")
            continue
        anim_type = animation_types[animation_file_names.index(file_name)]
        print("Adding", path, "to json with", anim_type, "type")
        add_to_anims(anims, path, anim_type)
    print()

    print("Completed scanning without errors!")
    normalize_rc(anims)
    write_anims(anims, root_path, os.path.split(root_path)[1])


while True:
    input_path = input("Please, type directory path, where .json file should be created:" + os.linesep)
    if input_path == "":
        break
    create_description(input_path)
