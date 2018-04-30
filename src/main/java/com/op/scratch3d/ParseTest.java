package com.op.scratch3d;

// This code was written by myself, Sean R. Owens, sean at guild dot net,
// and is released to the public domain. Share and enjoy. Since some
// people argue that it is impossible to release software to the public
// domain, you are also free to use this code under any version of the
// GPL, LPGL, Apache, or BSD licenses, or contact me for use of another
// license.  (I generally don't care so I'll almost certainly say yes.)
// In addition this code may also be used under the "unlicense" described
// at http://unlicense.org/ .  See the file UNLICENSE in the repo.

import com.owens.oobjloader.builder.Build;

public class ParseTest {

	public static void main(String[] argv) {

		for (String filename : argv) {

			try {
				Build builder = new Build();
				Parse obj = new Parse(builder, filename);
			} catch (java.io.FileNotFoundException e) {
				e.printStackTrace();
			} catch (java.io.IOException e) {
				e.printStackTrace();
			}
		}
	}
}