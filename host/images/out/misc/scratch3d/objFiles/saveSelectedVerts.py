import bpy
import bmesh
import sys

saveFile = open("D:\My Documents\My Eclipse\Scratch3D\Scratch3D\host\images\out\misc\scratch3d\objFiles\cubeHoleMax_sel.obj", "w")
obj=bpy.context.object
if obj.mode == 'EDIT':
    bm=bmesh.from_edit_mesh(obj.data)
    for v in bm.verts:
        if v.select:
            saveFile.write("v"+str(v.co.x)+' '+str(v.co.y)+' '+str(v.co.z)+' \n')
else:
    print("Object is not in edit mode.")

saveFile.close()