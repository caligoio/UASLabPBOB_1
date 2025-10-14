import math
import tkinter as tk
from tkinter import messagebox
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

# JENDELA UTAMA
root = tk.Tk()
root.title("Metode Regula Falsi")
root.geometry("950x700")
root.configure(bg="#1c1c1c")

# JUDUL
tk.Label(
    root,
    text="Metode Regula Falsi",
    font=("Helvetica", 18, "bold"),
    bg="#1c1c1c",
    fg="gold"
).pack(pady=10)

# FRAME INPUT
frame_input = tk.Frame(root, bg="#1c1c1c")
frame_input.pack(pady=5)

labels = [
    "Fungsi f(x):",
    "Batas bawah (a):",
    "Batas atas (b):",
    "Toleransi error:",
    "Iterasi maksimum:"
]
entries = []

for i, text in enumerate(labels):
    tk.Label(frame_input, text=text, bg="#1c1c1c", fg="white").grid(row=i, column=0, sticky="w", padx=5, pady=3)
    e = tk.Entry(frame_input, width=40)
    e.grid(row=i, column=1, padx=5, pady=3)
    entries.append(e)

# OUTPUT TEKS
output_text = tk.Text(root, width=110, height=15, bg="#222222", fg="white", insertbackground="white")
output_text.pack(pady=10)

# FRAME GRAFIK
plot_frame = tk.Frame(root, bg="#1c1c1c")
plot_frame.pack()

# PLOT FUNGSI
def tampilkan_plot(fx, hasil_x):
    def f(x):
        return eval(fx)

    fig, ax = plt.subplots(figsize=(6, 4), dpi=100)
    xs = [i / 10 for i in range(-10, 21)]
    ys = [f(x) for x in xs]

    ax.plot(xs, ys, color="gold", label="f(x)")
    ax.axhline(0, color="white", linestyle="--")
    ax.scatter(hasil_x, f(hasil_x), color="red", label="xr")
    ax.legend()
    ax.grid(color="gray", linestyle="dotted")

    fig.patch.set_facecolor("#1c1c1c")
    ax.set_facecolor("#222222")

    for widget in plot_frame.winfo_children():
        widget.destroy()

    canvas = FigureCanvasTkAgg(fig, master=plot_frame)
    canvas.get_tk_widget().pack()
    canvas.draw()

# LOGIKA REGULA FALSI
def jalankan_kode():
    try:
        fx = entries[0].get().strip()
        a = float(entries[1].get())
        b = float(entries[2].get())
        e = float(entries[3].get())
        iterasi = int(entries[4].get())

        if fx == "":
            messagebox.showerror("Error", "Fungsi f(x) tidak boleh kosong.")
            return

        def f(x):
            return eval(fx)

        output_text.delete("1.0", tk.END)

        if f(a) * f(b) >= 0:
            messagebox.showerror("Error", "f(a) dan f(b) harus memiliki tanda berbeda.")
            return

hasil = []
        hasil.append(f"\n{'Iter':<5} {'a':<10} {'b':<10} {'f(a)':<12} {'f(b)':<12} {'xr':<10} {'f(xr)':<12} {'f(xr)*f(a)':<12}")
        hasil.append("-" * 90)

        iter_akar = None
        xr = None

        for i in range(1, iterasi + 1):
            fa = f(a)
            fb = f(b)
            xr = b - fb * (b - a) / (fb - fa)
            fxr = f(xr)
            prod = fxr * fa

            hasil.append(f"{i:<5} {a:<10.6f} {b:<10.6f} {fa:<12.6f} {fb:<12.6f} {xr:<10.6f} {fxr:<12.6f} {prod:<12.6f}")

            if abs(fxr) < e and iter_akar is None:
                iter_akar = i

            if prod < 0:
                b = xr
            else:
                a = xr

for line in hasil:
            output_text.insert(tk.END, line + "\n")

        if iter_akar is not None:
            output_text.insert(tk.END, f"\nAkar mendekati ditemukan pada iterasi ke-{iter_akar}, x ≈ {xr}\n")
        else:
            output_text.insert(tk.END, "\nAkar tidak tercapai dalam toleransi error yang diberikan.\n")

        tampilkan_plot(fx, xr)

    except Exception as err:
        messagebox.showerror("Error", f"Terjadi kesalahan: {err}")

# FUNGSI RESET
def reset_semua():
    for e in entries:
        e.delete(0, tk.END)
    output_text.delete("1.0", tk.END)
    for widget in plot_frame.winfo_children():
        widget.destroy()

# TOMBOL HITUNG & RESET
button_frame = tk.Frame(root, bg="#1c1c1c")
button_frame.pack(pady=5)

tk.Button(
    button_frame,
    text="Hitung",
    command=jalankan_kode,
    bg="gold",
    fg="black",
    font=("Helvetica", 10, "bold"),
    width=12
).grid(row=0, column=0, padx=5)

tk.Button(
    button_frame,
    text="Reset",
    command=reset_semua,
    bg="gray",
    fg="white",
    font=("Helvetica", 10, "bold"),
    width=12
).grid(row=0, column=1, padx=5)

root.mainloop()
