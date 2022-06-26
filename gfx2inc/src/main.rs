// gfx2inc

use std::env;
use std::fs::File;
use std::io::Write;
use std::process::exit;
use image::{DynamicImage, GenericImageView, Pixel};
use image::io::Reader as ImageReader;

fn main() {
    let version_string = format!("gfx2inc {} ({})", env!("VERGEN_BUILD_SEMVER"), env!("VERGEN_GIT_SHA_SHORT"));
    println!("{}", version_string);

    let args: Vec<String> = env::args().collect();
    if args.len() != 5 {
        println!("Usage: {} <tile width> <tile height> <input> <output>", args[0]);
        exit(1);
    }

    let tile_width = args[1].parse::<u32>().unwrap();
    let tile_height = args[2].parse::<u32>().unwrap();
    let input_file_name = &args[3];
    let output_file_name = &args[4];

    // open the image
    let input_image = ImageReader::open(input_file_name).unwrap().decode().unwrap();
    let mut output_file = File::create(output_file_name).unwrap();

    // make sure the image has correct dimensions
    assert!(input_image.width() % tile_width == 0);
    assert!(input_image.height() % tile_height == 0);

    let number_of_tiles_wide = input_image.width() / tile_width;
    let number_of_tiles_high = input_image.height() / tile_height;

    let mut output_string = String::new();

    for row in 0..number_of_tiles_high {
        for column in 0..number_of_tiles_wide {
            write_tile(&input_image, &mut output_string, row, column, tile_width, tile_height);
        }
    }

    output_file.write_all(&output_string.into_bytes()).unwrap();
}

fn write_tile(input_image: &DynamicImage, output_string: &mut String, row: u32, column: u32, tile_width: u32, tile_height: u32) {
    for y in (row * tile_height)..((row + 1) * tile_height) {
        for x in (column * tile_width)..((column + 1) * tile_width) {
            let pixel = input_image.get_pixel(x, y);
            let pixel_channels = pixel.channels();
            output_string.push_str(&format!("data.32 0x{:02x}{:02x}{:02x}{:02x} ", pixel_channels[3], pixel_channels[2], pixel_channels[1], pixel_channels[0]));
        }
        output_string.push('\n');
    }
    output_string.push('\n');
}
