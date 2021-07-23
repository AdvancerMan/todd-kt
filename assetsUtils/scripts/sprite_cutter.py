from PIL import Image

if __name__ == '__main__':
    file, rows, columns = input("Please, enter file path, rows number and columns number separated by space: ").split()
    rows = int(rows)
    columns = int(columns)

    image = Image.open(file)
    width, height = image.size
    if width % columns != 0:
        raise ValueError(f"Image width ({width}) should be a multiple of columns number ({columns})")
    elif height % rows != 0:
        raise ValueError(f"Image height ({height}) should be a multiple of rows number ({rows})")
    width //= columns
    height //= rows

    for i in range(rows):
        for j in range(columns):
            cropped = image.crop((j * width, i * height, (j + 1) * width, (i + 1) * height))
            cropped.save(f"{j + 1 + i * columns}.png")
